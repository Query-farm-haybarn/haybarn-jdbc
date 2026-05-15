package farm.query.haybarn;

import static java.nio.charset.StandardCharsets.UTF_8;
import static farm.query.haybarn.DuckDBBindings.duckdb_scalar_function_set_error;
import static farm.query.haybarn.JdbcUtils.collectStackTrace;

import java.nio.ByteBuffer;

class DuckDBScalarFunctionWrapper {
    private final DuckDBScalarFunction function;

    DuckDBScalarFunctionWrapper(DuckDBScalarFunction function) {
        this.function = function;
    }

    public void execute(ByteBuffer functionInfo, ByteBuffer inputChunk, ByteBuffer outputVector) {
        try {
            DuckDBDataChunkReader inputReader = new DuckDBDataChunkReader(inputChunk);
            DuckDBWritableVector outputWriter = new DuckDBWritableVector(outputVector, inputReader.rowCount());
            function.apply(inputReader, outputWriter);
        } catch (Throwable throwable) {
            String trace = collectStackTrace(throwable);
            duckdb_scalar_function_set_error(functionInfo, trace.getBytes(UTF_8));
        }
    }
}
