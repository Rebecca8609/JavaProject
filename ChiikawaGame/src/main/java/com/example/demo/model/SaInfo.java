//(Mantle)
package com.example.demo.model;


import jakarta.persistence.*;

@Entity
@Table(name = "saInfo")
public class SaInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saId")
    private Integer saId;

    @Column(name = "saName", nullable = false)
    private String saName;

    @Column(name = "saEmail", nullable = false)
    private String saEmail;

    @Column(name = "saPassword", nullable = false)
    private String saPassword;

    @Column(name = "saTel", nullable = false)
    private String saTel;

    @Column(name = "saStatus", nullable = false)
    private Integer saStatus;

    // Getters and Setters
    public Integer getSaId() { return saId; }
    public void setSaId(Integer saId) { this.saId = saId; }

    public String getSaName() { return saName; }
    public void setSaName(String saName) { this.saName = saName; }

    public String getSaEmail() { return saEmail; }
    public void setSaEmail(String saEmail) { this.saEmail = saEmail; }

    public String getSaPassword() { return saPassword; }
    public void setSaPassword(String saPassword) { this.saPassword = saPassword; }

    public String getSaTel() { return saTel; }
    public void setSaTel(String saTel) { this.saTel = saTel; }

    public Integer getSaStatus() { return saStatus; }
    public void setSaStatus(Integer saStatus) { this.saStatus = saStatus; }
}
