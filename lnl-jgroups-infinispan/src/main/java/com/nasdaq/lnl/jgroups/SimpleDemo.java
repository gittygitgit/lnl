package com.nasdaq.lnl.jgroups;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

public class SimpleDemo {

	Map<String, Quote> bbo = new HashMap<String, Quote>();

	JChannel ch;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SimpleDemo demo = new SimpleDemo();
		demo.start();
	}

	private void start() throws Exception {
		ch = new JChannel("jgroups/tcp.xml");
		ch.setName("SimpleDemoChannel");
		ch.setReceiver(new SimpleReceiver());
		ch.setDiscardOwnMessages(true);
		ch.connect("test");
	

		loop();
	}

	private void loop() throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String userInput;
		while ((userInput = reader.readLine()) != null) {
			StringTokenizer msgTypeTokenizer = new StringTokenizer(userInput, ":");
			char msgType = Character.valueOf(msgTypeTokenizer.nextToken().charAt(0));
			switch (msgType) {
			case 'P':
				msgTypeTokenizer = new StringTokenizer(userInput.substring(2), " ");
				String und = msgTypeTokenizer.nextToken();
				bbo.put(und,
						new Quote(und, new BigDecimal(msgTypeTokenizer.nextToken()), new BigDecimal(msgTypeTokenizer
								.nextToken())));
				ch.send(new Message(null, (userInput).getBytes()));
				break;
			case 'G':
				System.out.println(bbo.get(userInput.substring(2)));
				break;

			default:
				break;
			}

		}
	}

	private class SimpleReceiver extends ReceiverAdapter {
		@Override
		public void viewAccepted(View view) {
			super.viewAccepted(view);
			System.out.println("View accepted: " + view.toString());
		}

		@Override
		public void receive(Message msg) {
			String payload = new String(msg.getBuffer());
			StringTokenizer st = new StringTokenizer(payload, ":");
			char msgType = Character.valueOf(st.nextToken().charAt(0));

			switch (msgType) {
			case 'P':
				System.out.println("quote");
				System.out.println("Received message: [msg=" + new String(msg.getBuffer()) + "]");
				st = new StringTokenizer(new String(msg.getBuffer()).substring(2), " ");
				String und = st.nextToken();
System.out.println(und);
				bbo.put(und,
						new Quote(und, new BigDecimal(st.nextToken()), new BigDecimal(st
								.nextToken())));
				break;
			default:
//				System.out.println(new String(msg.getBuffer()));
			}
		}

		@Override
		public void getState(OutputStream output) throws Exception {
			super.getState(output);
		}
	}

	private class Quote {
		String und;
		BigDecimal bid;
		BigDecimal ask;

		public Quote(String und, BigDecimal bid, BigDecimal ask) {
			super();
			this.und = und;
			this.bid = bid;
			this.ask = ask;
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 37).append(this.bid).append(this.ask).append(this.und)
					.toHashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (obj.getClass() != getClass()) {
				return false;
			}
			Quote rhs = (Quote) obj;
			return new EqualsBuilder().appendSuper(super.equals(obj)).append(bid, rhs.bid)
					.append(ask, rhs.ask).append(und, rhs.und).isEquals();
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this).append("und", und).append("bid", bid)
					.append("ask", ask).toString();
		}
	}
}
