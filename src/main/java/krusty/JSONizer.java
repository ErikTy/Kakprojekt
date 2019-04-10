package krusty;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Auxiliary class for automatically translating a ResultSet to JSON
 */
public class JSONizer {
	public static String toJSON(ResultSet rs, String name) throws SQLException {
		StringBuilder sb = new StringBuilder();
		ResultSetMetaData meta = rs.getMetaData();
		boolean first = true;
		sb.append("{\n");
		sb.append("  \"" + name + "\": [\n");
		while (rs.next()) {
			if (!first) {
				sb.append(",");
				sb.append("\n");
			
			}
			first = false;
			sb.append("    {");
			for (int i = 1; i <= meta.getColumnCount(); i++) {
				String label = meta.getColumnLabel(i);
				String value = getValue(rs, i, meta.getColumnType(i));
				sb.append("\"" + label + "\": " + value);
				if (i < meta.getColumnCount()) {
					sb.append(", ");
				}
			}
			sb.append("}");
		}
		sb.append("\n");
		sb.append("  ]\n");
		sb.append("}\n");
		return sb.toString();
	}

	private static String getValue(ResultSet rs, int i, int columnType) throws SQLException {
		switch (columnType) {
		case java.sql.Types.INTEGER:
			return String.valueOf(rs.getInt(i));
		case java.sql.Types.REAL:
		case java.sql.Types.DOUBLE:
		case java.sql.Types.FLOAT:
			return String.valueOf(rs.getDouble(i));
		default:
			return "\"" + rs.getString(i) + "\"";
		}
	}
}
