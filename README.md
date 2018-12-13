# Usage
See `sample` directory

# Build
```bash
./gradlew publish
```

# Set Secrets
```bash
digdag secrets --local --project sample --set @sample/secrets.json
```

# Run
```bash
digdag run --project sample plugin.dig -p repos=`pwd`/build/repo --session "2018-12-13 11:11:11"
```