package fantazia_szoft.twitch_foundry_spring.dto;


public class RollDTO {
    private String name;
    private String formula;
	public String getName() {
		return name;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public void setName(String name) {
		this.name = name;
	}
	public RollDTO(String name, String formula) {
		super();
		this.name = name;
		this.formula = formula;
	}
	
  
}
