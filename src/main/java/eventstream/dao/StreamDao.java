package eventstream.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import eventstream.domain.Stream;

/**
 * 
 *
 * @author Nilesh Bhosale
 */
@Transactional
public interface StreamDao extends CrudRepository<Stream, Long> {

	List<Stream> findByNounAndVerbAndUserid(String noun, String verb, int userid);
}