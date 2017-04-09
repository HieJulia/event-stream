package eventstream.util;

import java.util.ResourceBundle;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 
 *
 * @author Nilesh Bhosale
 */
@Component("applicationContextProvder")
public class ApplicationContextProvider implements ApplicationContextAware {

	private static ApplicationContext context;
	public static ResourceBundle bundle = ResourceBundle.getBundle("application");

	public ApplicationContext getApplicationContext() {
		return context;
	}

	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		context = ac;
	}
}