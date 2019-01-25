## Component: price calculation service

This will just be a super-simple REST API to return a price; it's just here to
demonstrate how a synchronous API can be included in the architecture.

API endpoint: `/price?size={small|medium|large}`

Example output: `{"size": "medium", "price": 15.95}`

## Running it:

```
gradle installDist
export API_PORT=8080
./build/install/price-api/bin/price-api
```

