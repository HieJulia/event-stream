package eventstream.dao;

import org.springframework.data.repository.CrudRepository;

import eventstream.domain.Stream;

import java.util.List;

import javax.transaction.Transactional;

/**
 * 
 *
 * @author Nilesh Bhosale
 */
@Transactional
public interface StreamDao extends CrudRepository<Stream, Long> {

	List<Stream> findByNounAndVerbAndUserid(String noun, String verb, int userid);
}