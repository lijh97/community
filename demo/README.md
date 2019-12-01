## 脚本 ##
```sql
CREATE TABLE USER 
{
    ID INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    ACCOUNT_ID VARCHAR(100),
    NAME VARCHAR(50),
    TOKEN VARCHAR (36),
    GMT_CREAT BIGINT,
    GMT_MODIFIED BIGINT
};
```
```sql
create table comment
(
	parent_id BIGINT not null comment '父类ID',
	type int not null comment '父类类型',
	id BIGINT auto_increment,
	commentator int not null comment '评论人',
	gmt_create BIGINT not null comment '创建时间',
	gmt_modified BIGINT not null comment '更新时间',
	like_count BIGINT default 0 not null comment '点赞数',
	content CHAR(50) not null,
	constraint comment_pk
		primary key (id)
);
```

```bash
mvn flyway:migrate
mvn -Dmybatis.generator.overwrite=true mybatis-generator:generate
```