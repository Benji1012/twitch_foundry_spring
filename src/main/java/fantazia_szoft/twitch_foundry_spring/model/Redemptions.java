package fantazia_szoft.twitch_foundry_spring.model;

import jakarta.persistence.*;

@Entity
@Table
public class Redemptions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String chanelId;
   private String userName;
   
    
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
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getChanelId() {
		return chanelId;
	}
	public void setChanelId(String chanelId) {
		this.chanelId = chanelId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	@Override
	public String toString() {
		return "Redemptions [id=" + id + ", userId=" + userId + ", chanelId=" + chanelId + ", userName=" + userName
				+ "]";
	}
	
}

