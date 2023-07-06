# DEPLOYMENT

Guide to deploy the Currency Converter backend into a production demo server.

For now this will be a small set of manual steps, but later we may want to automate this via the CI pipeline, by building the docker image and the mobile app binary for the release.

## CLONE

```
https://github.com/approov/currency-converter-demo.git && cd currency-converter-demo
```

## ENVIRONMENT

Copy the `.env.example`:

```
cp .env.example .env
```

### The free API Key for the Third Party Service

In order to use the third party service for the currency rates we need to register for a free API key [here](https://free.currencyconverterapi.com/free-api-key) and add it to the `.env` file:

```
API_KEY=free=api-key-here
```

### The API key for the Mobile App

Before implementing Approov the mobile app identifies itself to the backend with an API key, thus we need to set it to the same one released with the mobile app:

```
MOBILE_API_KEY=base64-encoded-api-key
```

### The Appoov secret

The `v2/*` endpoints are protected by the Approov Token, thus we need to set the Approov secret for `currency-converter.demo.approov.io`.

Get the Approov secret with:

```
approov secret /path/to/administration.tok -get base64
```

Add it to the `.env` file:

```
APPROOV_BASE64_SECRET=approov-base64-encoded-secret-here
```

## HOW TO RUN

### Bring the API up

```
sudo docker-compose up -d
```

### Bring the API down

```
sudo docker-compose down
```

### Check the Logs

```
sudo docker-compose logs --follow --tail 20
```
