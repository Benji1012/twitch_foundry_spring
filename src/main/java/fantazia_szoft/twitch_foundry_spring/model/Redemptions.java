package fantazia_szoft.twitch_foundry_spring.model;

import jakarta.persistence.*;

@Entity
@Table
public class Redemptions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String user_id;
    private String chanel_id;
   private String user_name;
   
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Redemptions() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getChanel_id() {
		return chanel_id;
	}
	public void setChanel_id(String chanel_id) {
		this.chanel_id = chanel_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	@Override
	public String toString() {
		return "Redemptions [id=" + id + ", user_id=" + user_id + ", chanel_id=" + chanel_id + ", user_name="
				+ user_name + "]";
	}
	
}

