package com.experiment.dsa1;

import com.experiment.dsa1.authenticationandauthorization.AuthorizationCode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Iterator;
import java.util.Stack;

@EnableScheduling
@SpringBootApplication
public class Dsa1Application {

	public static void main(String[] args)  throws  Exception {
		SpringApplication.run(Dsa1Application.class, args);

		/*
		ConfigurableApplicationContext context = SpringApplication.run(Dsa1Application.class, args);

		AuthorizationCode authco = context.getBean(AuthorizationCode.class);
		authco.getAuthorizationCode();
	*/

		/*Stack<Integer> st = new Stack<>();
		Iterator<Integer> iter = st.iterator();
		while(iter.hasNext()){
			System.out.println(iter.next());
		}*/
	}

}
