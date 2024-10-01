package com.project.app;

import com.project.app.model.ApplicationUser;
import com.project.app.model.Role;
import com.project.app.model.rooms.RoomConfig;
import com.project.app.repository.RoleRepository;
import com.project.app.repository.UserRepository;
import com.project.app.repository.rooms.RoomArtistRepository;
import com.project.app.repository.rooms.RoomConfigRepository;
import com.project.app.repository.rooms.RoomRepository;
import com.project.app.repository.rooms.RoomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.oauth2.client.OAuth2ClientSecurityMarker;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class AppApplication {

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private RoomConfigRepository roomConfigRepository;

	@Autowired
	private RoomUserRepository roomUserRepository;

	@Autowired
	private RoomArtistRepository roomArtistRepository;

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

	@Bean
	CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncode){
		return args ->{
			if(roleRepository.findByAuthority("ADMIN").isPresent()) {
				roomUserRepository.deleteAll();
				roomArtistRepository.deleteAll();
				roomConfigRepository.deleteAll();
				roomRepository.deleteAll();
				return;
			}
			Role adminRole = roleRepository.save(new Role("ADMIN"));
			roleRepository.save(new Role("USER"));

			Set<Role> roles = new HashSet<>();
			roles.add(adminRole);

			ApplicationUser testUser = new ApplicationUser(1, "testUser", "test@gmail.com", passwordEncode.encode("password"), roles);

			userRepository.save(testUser);

		};
	}
}
