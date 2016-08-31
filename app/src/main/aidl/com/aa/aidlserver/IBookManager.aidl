// IBookManager.aidl
package com.aa.aidlserver;
import com.aa.aidlserver.Book;

interface IBookManager {
    //获取书本列表
    List<Book> getBookList();
    //添加书本
    void addBook(in Book book);
    //删除书本
    void deleteBook(in Book book);
}
