# Decoupled Demo

_A demo of how you could build a shop site out of decoupled microservices with event-sourcing_

## Overview of components

Primary components:
- item browsing website
- price calculation service
- item search service
- item management website (ie. create/update items)

## Component: Item management site

This will consist of a Postgresql database and a simple website.

It will let the user create a new "item", or select an existing item, and
then edit it.

In the demo this will be very simple -- no auth, and searching for items may as well just be text searches against the database.

Items will have an image, title, and description.
Uploaded images will get stored in S3, in two versions -- original and thumbnail.
(with a path like "/image/$id/original.jpg")

Whenever an item is created or updated, or deleted, an event is sent to Kinesis.

```
{
  "event": "item-created",
  "id": "08dfde4c-41af-4c14-a4c6-a5e03c9bc3a8",
  "title": "Fantastic Work",
  "description": "My latest creation",
  "image": "s3://image/08dfde4c-41af-4c14-a4c6-a5e03c9bc3a8/original.jpg",
  "thumbnail": "s3://image/08dfde4c-41af-4c14-a4c6-a5e03c9bc3a8/thumbnail.jpg"
}
```

## Component: price calculation service

This will just be a super-simple REST API to return a price; it's just here to
demonstrate how a synchronous API can be included in the architecture.

API endpoint: `/price?size={small|medium|large}`

Example output: `{"size": "medium", "price": 15.95}`

## Component: Item search service

This component allows a free-text search to be run against the available items.

Simple REST API: `/search?q=example+works`

Example output:
```
{
  "results": [
    {
      "id": "08dfde4c",
      "title": "Fantastic work",
      "description": "My latest creation",
      "image": "s3://image/08dfde4c-41af-4c14-a4c6-a5e03c9bc3a8/original.jpg",
      "thumbnail": "s3://image/08dfde4c-41af-4c14-a4c6-a5e03c9bc3a8/thumbnail.jpg"
    },
    {
      "id": "a5e03c9bc3a8",
      "title": "Work of fantasticness",
      "description": "Not fantastic enough for words",
      "image": "s3://image/a5e03c9bc3a8-a4c6-08dfde4c/original.jpg",
      "thumbnail": "s3://image/a5e03c9bc3a8-a4c6-08dfde4c/thumbnail.jpg"
    }
  ]
}
```

For the purposes of our demo, this will be another microservice, probably with an ElasticSearch backend.

This component will listen for events, and update the elasticsearch backend as events are received.

## Component: Item browsing website

This is the main front-end for our imaginary users.

It provides a website that lets users view items, search for items.

Landing page: Search text box and button. (Classic "Search" and "I'm feeling lucky" buttons)

Below that, a few thumbnails of "trending items", tiled.

Search results: Tiles of item thumbnails with titles.
Clicking item takes you to...

Item detail page: Full-size item image, title, description.

Also includes a size selector, which dynamically updates a price. (Using the pricing service API)

App wil be consist of:
- A Kotlin event-receiver app
- some javascript files
- nginx, which proxies to either the static javascript, or the Kotlin app,
  or to the /items/ directory.
- The /items/ directory, which contains JSON files.

It will work by:
- Listening for events about items being created/updated/deleted
- Upon receiving event, it saves the JSON into `/items/$id/item.json`
- The javascript runs the whole site.
- alternatively, use Postgres and JSON blobs -- two columns, id, and the json blob, with a Kotlin API handling the lookup.

Trending items will be a hardcoded JSON list initially; as a stretch goal: that list will be maintained by item-sold events.

Searching will work with the Javascript querying the search API directly.
Likewise, price checks go directly as well.

Item detail pages work by it querying the local server for the JSON file, and using that to render an items detail page.

Images are all served off S3, simply by using that S3 url directly. (ie. it's a public bucket)


## Stretch goal: Checkout page, checkout events, trending items

Add a checkout page to the consumer-facing app; no actual payment method, but let the user "buy" things.

Buying an item triggers an item-purchased event (id, price).

An aggregator service listens to those events, just counting the number of times a particular id has been purchased in the last 60 minutes, and emits an event of the current trending items every 10 minutes.

ie. Database table is: id, timestamp default now()

Queries are:
- `delete from table where timestamp < now() - 60 minutes`
- `select id, count(*) as n from table group by 1 order by 2 desc limit 10`


## For neat demo purposes, listen to production

In order to do a cool demo, we should build something that listens to production
work-created events, and then converts them into our simple format, then re-emits them into our stream.

