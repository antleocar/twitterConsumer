# Consumer for Twitter Stream API with protobuf schema

## Description

Consumer for Twitter Stream API, filtered by location and send data with protobuf structure.

### Install Protobuf

To install protobuf, personally I run:
```
brew install protobuf
```

#### Generate Class

To generate the protobuf class you should run this command:
```
protoc -I=$SRC_DIR --java_out=$DST_DIR $SRC_DIR/$name.proto
```

More info in:
* [Protobuf](https://developers.google.com/protocol-buffers/docs/javatutorial)
