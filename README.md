# digdag-plugin-s3-touch
  
[![CircleCI](https://circleci.com/gh/tosametal/digdag-plugin-s3-touch.svg?style=svg)](https://circleci.com/gh/tosametal/digdag-plugin-s3-touch)

## Usage
```bash
_export:
  plugin:
    repositories:
      - https://jitpack.io/
    dependencies:
      - com.github.tosametal:digdag-plugin-s3-touch:0.0.2

  s3_touch:
    bucket_name: bucket_name
    access_key: access_key
    secret_key: secret_key
    service_endpoint: service_endpoint
    default_region: default_region
    access_control_list: access_control_list
    # proxy_host: proxy_host
    # proxy_port: proxy_port

+task:
  s3_touch>: path/to/flag
```
- select access_control_list from `private`, `public-read`, `public-read-write`, `authenticated-read`, `log-delivery-write`, `bucket-owner-read`, `bucket-owner-full-control` and `aws-exec-read`
- proxy_host and proxy_port is *optional*
  
  
See `sample` directory

## Build
```bash
./gradlew publish
```

## Run a sample workflow in local environment
(1)Remove `.digdag` directory
```bash
rm -rf .digdag
```

(2)Build plugin
```bash
./gradlew publish
```

(3)Set secrets
```bash
cp sample/secrets.json sample/secrets.prod.json
```

overwrite `sample/secrets.prod.json` with your settings

```bash
digdag secrets --local --project sample --set @sample/secrets.prod.json
```
  
(4)Run a sample workflow
```bash
digdag run --project sample plugin.dig -p repos=`pwd`/build/repo --session "2018-12-15 00:00:00"
```

## Code Format
```bash
./gradlew spotlessApply
```

## Test
```bash
 ./gradlew test
```
