package com.community.geunsu;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BookmarkController {
	
	@Autowired
	 private BookmarkMapper   bookmarkMapper;
	
	@ReqestMapping("/Bookmark/List")
	public ModelAndView list( BookMarkDTO bookmarkDTO ) {
		
		List<BookmarkDTO> bookmarkList = bookmarkMapper.getbookmarkList( bookmarkDTO);
		
		bookmarkDTO                    =    bookmarkMapper.getbookmark(bookmarkDTO);
		
	}
	
	// --------------------------------------------------
	
	ModelAndView mv = new ModelAndView();
	mv.addObject("bookmarkList", bookmarkList);
	mv.addObject("menuDTO", menuDTO);
	mv.addObject("boardList", boardList);
	mv.setViewName( "bookmark/list");
	return mv;
	
	
}

@RequestMapping("/bookmark/WriteForm")
public  ModelAndView writeForm( BookMarkDTO bookmarkDTO) {
	
	List<BookMarkDTO> bookmarkList = bookmarkMapper.getBookMarkList();
	
	bookmarkDTO                    = bookmarkMapper.getBookMark( bookmarkDTO );
	
	ModelAndView mv = new ModelAndView();
	mv.addObject("bookmarkList, bookmarkList");
	mv.addObject("bookmarkDTO, bookmarkDTO");
	mv.setViewName( "bookmark/write");
	return          mv;	
	
}

    @RequestMapping("/BookMark/Write")
    public  ModelAndView  write( BookMarkDTO BookMarkDTO ) {
    	
    	bookmarkMapper.insertBookMark( bookmarkDTO );
    	
    	Long user_id = bookmarkDTO.getuser_id();
    	
    	ModelAndView mv = new ModelAndView();
    	mv.setViewName("redirect:/Board/List?menu_id=" + menu_id);
    	return mv;
    	
    }
    
    @RequestMapping("/Bookmark/View")
    public  ModelAndView  view(  BookMarkDTO bookmarkDTO ) {
    	
    	List<BookMarkDTO>  bookmarkList = bookmarkMapper.getBookmarkList();
    	
    	bookmarkMapper.incHit( bookmarkDTO);
    	
    	BookmarkDTO bookmark  =  bookmarkMapper.getBookmark( bookmarkDTO );
    	
    	Long          user_id      =  bookmark.getUser_id();
    	BookMarkDTO   bookmarkDTO  =  new BookMarkDTO(user_id, null, 0);
    	bookmarkDTO                =  bookmarkMapper.getBookmark( bookmarkDTO );
    	
    	
    }





















