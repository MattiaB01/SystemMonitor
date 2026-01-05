package main;

public class Cpu_Model {

	private int cpu;
	private int ram;
	private int space;
	public int getCpu() {
		return cpu;
	}
	public void setCpu(int cpu) {
		this.cpu = cpu;
	}
	public int getRam() {
		return ram;
	}
	public void setRam(int ram) {
		this.ram = ram;
	}
	public int getSpace() {
		return space;
	}
	public void setSpace(int space) {
		this.space = space;
	}
	public Cpu_Model(int cpu, int ram, int space) {
		super();
		this.cpu = cpu;
		this.ram = ram;
		this.space = space;
	}
	
	
	
	
}
