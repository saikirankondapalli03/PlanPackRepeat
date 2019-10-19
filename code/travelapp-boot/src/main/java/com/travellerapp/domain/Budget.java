package com.travellerapp.domain;

import java.math.BigDecimal;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Budget {
	@Id
	private ObjectId _id;

	private BigDecimal totalBudget;
	private BigDecimal currentSpending;

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public BigDecimal getTotalBudget() {
		return totalBudget;
	}

	public void setTotalBudget(BigDecimal totalBudget) {
		this.totalBudget = totalBudget;
	}

	public BigDecimal getCurrentSpending() {
		return currentSpending;
	}

	public void setCurrentSpending(BigDecimal currentSpending) {
		this.currentSpending = currentSpending;
	}

}
