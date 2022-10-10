package com.tuum.core.banking.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.ObjectUtils;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;
import java.util.Date;


@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class CoreBankingBaseEntity {

  @CreatedDate()
  @Column(name = "created_date")
  private Date createdTime;

  @LastModifiedDate
  @Column(name = "last_modified_date")
  private Date lastModifiedTime;

}
