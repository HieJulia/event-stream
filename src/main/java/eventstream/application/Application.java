package eventstream.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.beans.PropertyEditorSupport;

/**
 * Created by Nilesh Bhosale
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /*public static final class ActionEnumEditor extends PropertyEditorSupport {

        @Override
        public void setAsText(final String text)
        {
            setValue(ActionEnum.valueOf(text.toUpperCase()));
        }

    }*/
}
