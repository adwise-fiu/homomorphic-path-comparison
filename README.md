# Level-Site-PPDT

## Libraries
* crypto.jar library is from this [repository](https://github.com/AndrewQuijano/Homomorphic_Encryption)

## Installation
It is a requirement to install [SDK](https://sdkman.io/install) to install Gradle.
You need to install the following packages, to ensure everything works as expected
```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install gradle
```

## Using the Code
For now you can run 
```bash
gradle run -PchooseRole=CleartextPathsComparison
```