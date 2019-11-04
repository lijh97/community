package cn.edu.sysu.bookmanager.bookmanager.dao;

import cn.edu.sysu.bookmanager.bookmanager.model.Book;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface BookDAO {

    @Insert("insert into book (name, author, price) values (#{name}, #{author}, #{price})")
    int addBook(Book book);

    @Select("select * from book where id = #{id}")
    Book selectBookById(@Param("id") int id);
    
    @Select("select * from book where name = #{name}")
    Book selectBookByName(@Param("name") String name);
}
