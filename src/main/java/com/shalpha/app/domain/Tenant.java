package com.shalpha.app.domain;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="tenant")
//@FilterDef(name = "TENANT_FILTER", parameters = {@ParamDef(name = "tenantId", type = "long")})
//@Filter(name="TENANT_FILTER", condition="id = :tenantId")
public class Tenant {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private long id;

	@Column(name="created_date")
	private Timestamp createdDate;

	@Column(name="status")
	private Boolean status;

	@Column(name="issuer_uri")
	private String issuerUri;

	private String name;

	private String type;

	@Column(name="last_modified_date")
	private Timestamp updatedDate;

	//bi-directional many-to-one association to TblUser
//	@JsonIgnore
//	@OneToMany(mappedBy="tenant")
//	private Set<User> users;
	
	@OneToMany(mappedBy = "tenant")
    private Set<User> users = new HashSet<>();

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the createdDate
	 */
	public Timestamp getCreatedDate() {
		return createdDate;
	}

	/**
	 * @return the status
	 */
	public Boolean getStatus() {
		return status;
	}

	/**
	 * @return the issuerUri
	 */
	public String getIssuerUri() {
		return issuerUri;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the updatedDate
	 */
	public Timestamp getUpdatedDate() {
		return updatedDate;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Boolean status) {
		this.status = status;
	}

	/**
	 * @param issuerUri the issuerUri to set
	 */
	public void setIssuerUri(String issuerUri) {
		this.issuerUri = issuerUri;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @param updatedDate the updatedDate to set
	 */
	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	/**
	 * @return the users
	 */
	public Set<User> getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(Set<User> users) {
		this.users = users;
	}

}
