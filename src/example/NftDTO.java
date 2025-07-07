package example;

public class NftDTO {
	public int id;
	public int th;
	public int energyEfficiency;
	public double priceUSDT;
	public double Ratio;
	
	public NftDTO(int id, int th, int energyEfficiency, double priceUSDT) {
		this.id = id;
		this.th = th;
		this.energyEfficiency = energyEfficiency;
		this.priceUSDT = priceUSDT;
		this.Ratio = priceUSDT / th;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("id = ").append(this.id).append("\nth = ").append(th)
		.append("\nenergyEfficiency = ").append(this.energyEfficiency).append("\npriceUSDT = ").append(this.priceUSDT).append("\nRatio = ").append(this.Ratio);
		return str.toString();
	}
}
