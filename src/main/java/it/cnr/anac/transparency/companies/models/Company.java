/*
 * Copyright (C) 2023 Consiglio Nazionale delle Ricerche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package it.cnr.anac.transparency.companies.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Entity che rappresenta i dati di un Ente pubblico.
 *
 * @author Cristian Lucchesi
 */
@ToString(callSuper = true)
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "companies")
@Entity
public class Company extends MutableModel {

  private static final long serialVersionUID = 4740036434463012854L;

  private String codiceIpa;
  private String denominazioneEnte;
  private String codiceFiscaleEnte;
  private String tipologia;
  private String codiceCategoria;
  private String codiceNatura;
  private String acronimo;
  private String sitoIstituzionale;
  @Enumerated(EnumType.STRING)
  private CompanySource sorgente;
  private LocalDate dataCancellazione;

  @ManyToOne
  @JoinColumn(name = "municipality_id")
  private Municipality comune;

  @OneToOne
  private Address address;

  private String codiceComuneIstat;
  private String codiceCatastaleComune;
  private String cap;
  private String indirizzo;

  private String nomeResponsabile;
  private String cognomeResponsabile;
  private String titoloResponsabile;

  @Column(name="mail_1")
  private String mail1;
  @Column(name="tipo_mail_1")
  private String tipoMail1;
  @Column(name="mail_2")
  private String mail2;
  @Column(name="tipo_mail_2")
  private String tipoMail2;
  @Column(name="mail_3")
  private String mail3;
  @Column(name="tipo_mail_3")
  private String tipoMail3;
  @Column(name="mail_4")
  private String mail4;
  @Column(name="tipo_mail_4")
  private String tipoMail4;
  @Column(name="mail_5")
  private String mail5;
  @Column(name="tipo_mail_5")
  private String tipoMail5;

  @Transient
  public String getDenominazioneComune() {
    return comune != null ? comune.getDenominazione() : null;
  }

  @Transient
  public String getDenominazioneUnitaSovracomunale() {
    return comune != null ? comune.getDenominazioneUnitaSovracomunale() : null;
  }

  @Transient
  public String getDenominazioneRegione() {
    return comune != null ? comune.getDenominazioneRegione() : null;
  }

}