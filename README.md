# Privacy-Preserving Drone Navigation Through Homomorphic Encryption for Collision Avoidance
[![Build Gradle project](https://github.com/adwise-fiu/homomorphic-path-comparison/actions/workflows/build-gradle-project.yml/badge.svg)](https://github.com/adwise-fiu/homomorphic-path-comparison/actions/workflows/build-gradle-project.yml)

## Installation
It is a requirement to install [SDK](https://sdkman.io/install) to install Gradle.
You need to install the following packages, to ensure everything works as expected
```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install gradle
```

## Using the Code
For now, you can run 
```bash
gradle run -PchooseRole=CleartextPathsComparison
```

We will be using 2048-bit keys.

For the experiment, on one VM, run the following
```bash
gradle run -PchooseRole=PathsAlice --args '<PATH-FILE> 127.0.0.1 9000'
```

On the other VM run the following
```bash
gradle run -PchooseRole=PathsBob --args '<PATH-FILE> 9000'
```
