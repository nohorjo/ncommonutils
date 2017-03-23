package nohorjo.out;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatedPrintStream extends PrintStream {
	SimpleDateFormat sdf;

	public DatedPrintStream(OutputStream out, String dateFormat) {
		super(out);
		sdf = new SimpleDateFormat(dateFormat);
	}

	public DatedPrintStream(OutputStream out) {
		this(out, "ss.MM.yy HH:mm:ss  ");
	}

	@Override
	public void println(String x) {
		super.println(sdf.format(new Date()) + x);
	}
}
