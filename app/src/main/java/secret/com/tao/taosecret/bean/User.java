package secret.com.tao.taosecret.bean;

import java.sql.Timestamp;

/**
 * User entity. @author MyEclipse Persistence Tools
 */

public class User implements java.io.Serializable {

	// Fields

	private Integer id;
	private String phoneNum;
	private String code;
	private String token;
	private Timestamp lastTime;
	private String contact;
	private Timestamp regTime;

	// Constructors

	/** default constructor */
	public User() {
	}

	/** full constructor */
	public User(String phoneNum, String code, String token, Timestamp lastTime,
			String contact, Timestamp regTime) {
		this.phoneNum = phoneNum;
		this.code = code;
		this.token = token;
		this.lastTime = lastTime;
		this.contact = contact;
		this.regTime = regTime;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPhoneNum() {
		return this.phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Timestamp getLastTime() {
		return this.lastTime;
	}

	public void setLastTime(Timestamp lastTime) {
		this.lastTime = lastTime;
	}

	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public Timestamp getRegTime() {
		return this.regTime;
	}

	public void setRegTime(Timestamp regTime) {
		this.regTime = regTime;
	}

}