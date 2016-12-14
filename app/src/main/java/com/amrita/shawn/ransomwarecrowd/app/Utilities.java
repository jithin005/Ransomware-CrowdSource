package com.amrita.shawn.ransomwarecrowd.app;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Utilities {

	public class Connection {
		String src;
		String spt;
		String dst;
		String uid;


	}

	private final String getAddress(final String hexa) {
		try {
			final long v = Long.parseLong(hexa, 16);
			final long adr = (v >>> 24) | (v << 24) | ((v << 8) & 0x00FF0000)
					| ((v >> 8) & 0x0000FF00);
			return ((adr >> 24) & 0xff) + "." + ((adr >> 16) & 0xff) + "."
					+ ((adr >> 8) & 0xff) + "." + (adr & 0xff);
		} catch (Exception e) {
			Log.w("Ransom", e.toString(), e);
			return "-1.-1.-1.-1";
		}
	}

	private final String getAddress6(final String hexa) {
		try {
			final String ip4[] = hexa.split("0000000000000000FFFF0000");

			if (ip4.length == 2) {
				final long v = Long.parseLong(ip4[1], 16);
				final long adr = (v >>> 24) | (v << 24)
						| ((v << 8) & 0x00FF0000) | ((v >> 8) & 0x0000FF00);
				return ((adr >> 24) & 0xff) + "." + ((adr >> 16) & 0xff) + "."
						+ ((adr >> 8) & 0xff) + "." + (adr & 0xff);
			} else {
				return "-2.-2.-2.-2";
			}
		} catch (Exception e) {
			Log.w("Ransom", e.toString(), e);
			return "-1.-1.-1.-1";
		}
	}

	private final int getInt16(final String hexa) {
		try {
			return Integer.parseInt(hexa, 16);
		} catch (Exception e) {
			Log.w("Ransom", e.toString(), e);
			return -1;
		}
	}

	public ArrayList<String> getPIDConnections(String PID, String UID) {
		ArrayList<String> ipad = new ArrayList<String>();
		String k;

		try {
			BufferedReader in = new BufferedReader(new FileReader("/proc/"
					+ PID + "/net/tcp"));
			String line;

			while ((line = in.readLine()) != null) {
				line = line.trim();
				String[] fields = line.split("\\s+", 10);


				if (fields[0].equals("sl")) {
					continue;
				}

				Connection connection = new Connection();

				String src[] = fields[1].split(":", 2);
				String dst[] = fields[2].split(":", 2);

				connection.uid = fields[7];
				Log.d(connection.uid,UID);
				if(connection.uid.equals(UID)) {


					connection.src = getAddress(src[0]);

					connection.spt = String.valueOf(getInt16(src[1]));
					connection.dst = getAddress(dst[0]);
					k = getAddress(dst[0]);
					Log.d("ip",k);
					if (!k.equals("0.0.0.0") && !k.equals("-1.-1.-1.-1")) {
						ipad.add(k);
						k = getAddress(src[0]);
						if (!k.equals("0.0.0.0") && !k.equals("-1.-1.-1.-1")) {
							ipad.add(k);
						}
					}
				}

			}

			in.close();

			in = new BufferedReader(new FileReader("/proc/" + PID + "/net/udp"));

			while ((line = in.readLine()) != null) {
				line = line.trim();

				String[] fields = line.split("\\s+", 10);



				if (fields[0].equals("sl")) {
					continue;
				}

				Connection connection = new Connection();
				String src[] = fields[1].split(":", 2);
				String dst[] = fields[2].split(":", 2);
				connection.uid = fields[7];

				Log.d(connection.uid,UID);
				if(connection.uid.equals(UID)) {


					connection.src = getAddress(src[0]);
					connection.spt = String.valueOf(getInt16(src[1]));
					connection.dst = getAddress(dst[0]);
					k = getAddress(dst[0]);
					Log.d("ip",k);
					if (!k.equals("0.0.0.0") && !k.equals("-1.-1.-1.-1")) {
						ipad.add(k);
						k = getAddress(src[0]);
						if (!k.equals("0.0.0.0") && !k.equals("-1.-1.-1.-1")) {
							ipad.add(k);
						}
					}
				}

			}

			in.close();

			in = new BufferedReader(
					new FileReader("/proc/" + PID + "/net/tcp6"));

			while ((line = in.readLine()) != null) {
				line = line.trim();

				String[] fields = line.split("\\s+", 10);

				if (fields[0].equals("sl")) {
					continue;
				}

				Connection connection = new Connection();

				String src[] = fields[1].split(":", 2);
				String dst[] = fields[2].split(":", 2);

				connection.src = getAddress6(src[0]);
				connection.spt = String.valueOf(getInt16(src[1]));
				connection.dst = getAddress6(dst[0]);
				connection.uid = fields[7];
				Log.d(connection.uid,UID);
				if(connection.uid.equals(UID)) {

					k = getAddress6(dst[0]);
					Log.d("ip",k);
					if (!k.equals("0.0.0.0") && !k.equals("-2.-2.-2.-2")) {
						ipad.add(k);
						k = getAddress6(src[0]);
						if (!k.equals("0.0.0.0") && !k.equals("-2.-2.-2.-2")) {
							ipad.add(k);
						}
					}
				}

			}

			in.close();

			in = new BufferedReader(
					new FileReader("/proc/" + PID + "/net/udp6"));

			while ((line = in.readLine()) != null) {
				line = line.trim();

				String[] fields = line.split("\\s+", 10);

				if (fields[0].equals("sl")) {
					continue;
				}

				Connection connection = new Connection();

				String src[] = fields[1].split(":", 2);
				String dst[] = fields[2].split(":", 2);

				connection.src = getAddress6(src[0]);
				connection.spt = String.valueOf(getInt16(src[1]));
				connection.dst = getAddress6(dst[0]);
				connection.uid = fields[7];
				Log.d("checking",connection.uid+":"+UID);
				if(connection.uid.equals(UID)) {

					k = getAddress6(dst[0]);
					Log.d("ip",k);
					if (!k.equals("0.0.0.0") && !k.equals("-2.-2.-2.-2")) {
						ipad.add(k);
						k = getAddress6(src[0]);
						if (!k.equals("0.0.0.0") && !k.equals("-2.-2.-2.-2")) {
							ipad.add(k);
						}
					}
				}

			}

			in.close();


		} catch (Exception e) {
			Log.w("Ransom", e.toString(), e);
		}

		return ipad;
	}

}
