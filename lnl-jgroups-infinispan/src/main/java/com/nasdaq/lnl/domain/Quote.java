package com.nasdaq.lnl.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Quote implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
		return new ToStringBuilder(this).append("und", und).append("bid", bid).append("ask", ask)
				.toString();
	}
}
