# Usage
for production

```yaml
_export:
  plugin:
    repositories:
      - https://jitpack.io
    dependencies:
      - com.github.tosametal:digdag-plugin-s3-touch:0.0.1
  s3_touch:
    bucket_name: xxxx
    access_key: xxxx
    secret_key: xxxx
    default_region: xxxx
+step1:
  s3_touch>:
  filename: xxxxx
```


# Build
```bash
./gradlew publish
```

# Run
```bash
digdag run --project sample plugin.dig -p repos=`pwd`/build/repo --session "2018-12-13 11:11:11"
```