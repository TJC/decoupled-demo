## Component: Item management site

This will consist of a database and a simple website.

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

## Running it

```
gradle installDist
export API_PORT=8080
./build/install/uploader/bin/uploader
```

