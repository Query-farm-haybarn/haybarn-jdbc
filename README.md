# Haybarn JDBC driver

A JDBC driver for [Haybarn](https://github.com/Query-farm-haybarn/haybarn) — a
derived distribution of DuckDB ("Haybarn, powered by DuckDB"), published by
Query Farm LLC.

This is a hard fork of [duckdb/duckdb-java](https://github.com/duckdb/duckdb-java)
based on tag `v1.5.3.0`, with the Java package, Maven coordinates, JNI library
name, and JDBC URL prefix rebranded to Haybarn. The vendored `src/duckdb/`
engine tree is re-vendored from
[Query-farm-haybarn/haybarn](https://github.com/Query-farm-haybarn/haybarn) so
the driver embeds the Haybarn build of DuckDB (Haybarn trust root, Haybarn
extension repository URLs).

## Use

### Maven

```xml
<dependency>
  <groupId>farm.query.haybarn</groupId>
  <artifactId>haybarn_jdbc</artifactId>
  <version>1.5.5</version>
</dependency>
```

### Code

```java
import farm.query.haybarn.HaybarnDriver;       // SPI entry-point; loaded automatically
import farm.query.haybarn.DuckDBConnection;    // class names kept for source compat

try (Connection conn = DriverManager.getConnection("jdbc:haybarn::memory:");
     Statement stmt = conn.createStatement();
     ResultSet rs = stmt.executeQuery("SELECT version()")) {
    rs.next();
    System.out.println(rs.getString(1));   // v1.5.3
}
```

### Migration from `org.duckdb`

The JDBC URL prefix changes to `jdbc:haybarn:` and the Java package moves to
`farm.query.haybarn`. The class names themselves (`DuckDBConnection`,
`DuckDBAppender`, etc.) are kept verbatim so user code that casts to them
needs only an import-source change. The Driver SPI entry-point class is
renamed: `org.duckdb.DuckDBDriver` → `farm.query.haybarn.HaybarnDriver`.

```diff
- import org.duckdb.DuckDBDriver;
- import org.duckdb.DuckDBConnection;
+ import farm.query.haybarn.HaybarnDriver;
+ import farm.query.haybarn.DuckDBConnection;
- conn = DriverManager.getConnection("jdbc:duckdb::memory:");
+ conn = DriverManager.getConnection("jdbc:haybarn::memory:");
```

## Development

It's required to have a JDK installed to build.
Make sure the `JAVA_HOME` environment variable is set.

To build the driver, run `make release`. This produces:

- `build/release/haybarn_jdbc.jar`
- `build/release/haybarn_jdbc_tests.jar`

Run the tests:

```sh
make test
# or, equivalently:
java -cp "build/release/haybarn_jdbc_tests.jar:build/release/haybarn_jdbc.jar" \
  farm.query.haybarn.TestDuckDBJDBC
```

Run a single test by name:

```sh
java -cp "build/release/haybarn_jdbc_tests.jar:build/release/haybarn_jdbc.jar" \
  farm.query.haybarn.TestDuckDBJDBC test_valid_but_local_config_throws_exception
```

### Refresh the vendored engine

`src/duckdb/` is a vendored copy of the DuckDB source, as modified by the
Haybarn core fork. To roll forward to a newer Haybarn core, point a fresh
checkout of `Query-farm-haybarn/haybarn @ haybarn` at `vendor.py`:

```sh
python3 vendor.py --duckdb /path/to/haybarn-checkout
```

Then bump the version in `META-INF/MANIFEST.MF` and the publisher scripts.

### Scalar function usage examples

See [UDF.MD](UDF.MD).

## Distribution

- **Maven Central** at `farm.query.haybarn:haybarn_jdbc`. Per-platform native
  libraries are bundled as classified JARs (linux-amd64, linux-arm64,
  osx-universal, windows-amd64, windows-arm64) plus a fat `haybarn_jdbc.jar`
  containing all of them.
- Releases are GPG-signed with the Haybarn release key (the same key used by
  `Query-farm-haybarn/haybarn` GitHub releases).
- Release tags are `haybarn-v<version>`; `.github/workflows/haybarn-jdbc.yml`
  triggers on them.

## Attribution

Haybarn is independent of and not endorsed by the DuckDB Foundation. DuckDB
is a trademark of the DuckDB Foundation. See [`NOTICE`](NOTICE) for the
modifications this fork makes to the upstream `duckdb/duckdb-java` repository.
