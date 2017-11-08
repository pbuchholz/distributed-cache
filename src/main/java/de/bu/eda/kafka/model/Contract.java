package de.bu.eda.kafka.model;

public class Contract {

	public static final class ContractNumber {

		private long number;

		public ContractNumber() {

		}

		public ContractNumber(long number) {
			this.number = number;
		}

		public long getNumber() {
			return this.number;
		}

		public void setNumber(long number) {
			this.number = number;
		}

	}

	private ContractNumber contractNumber;

	public Contract() {

	}

	public Contract(long contractNumber) {
		this.contractNumber = new ContractNumber(contractNumber);
	}

	public ContractNumber getContractNumber() {
		return this.contractNumber;
	}

	public void setContractNumber(ContractNumber contractNumber) {
		this.contractNumber = contractNumber;
	}

}