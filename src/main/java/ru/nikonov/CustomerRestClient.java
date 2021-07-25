package ru.nikonov;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.nikonov.entity.User;

import java.util.List;

@Component
public class CustomerRestClient {
    private final RestTemplate restTemplate;
    private final String URL = "http://91.241.64.178:7081/api/users";
    private String JSESSIONID;
    private HttpHeaders headers = new HttpHeaders();
    private String code = "";

    @Autowired
    public CustomerRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<User> getUsers() {
        ResponseEntity<List<User>> responseEntity = restTemplate.exchange(URL
                , HttpMethod.GET
                , null
                , new ParameterizedTypeReference<List<User>>() {});
        List<User> users = responseEntity.getBody();
        JSESSIONID = responseEntity.getHeaders().get("Set-Cookie").get(0);
        headers.add("Cookie", JSESSIONID);

        return users;
    }

    public User[] getUsersInArray() {
        ResponseEntity<User[]> responseEntity = restTemplate.getForEntity(URL, User[].class);
        User[] users = responseEntity.getBody();
        JSESSIONID = responseEntity.getHeaders().get("Set-Cookie").get(0);
        headers.add("Cookie", JSESSIONID);

        return users;
    }


    public void saveUser(User user) {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(URL
                , new HttpEntity<>(user, headers)
                , String.class);

        System.out.println("New user was added to DB");
        code += responseEntity.getBody();
    }

    public void updateUser(User user) {
        ResponseEntity<String> responseEntity = restTemplate.exchange(URL
                , HttpMethod.PUT
                , new HttpEntity<>(user, headers)
                , String.class);
        System.out.println("User with ID " + user.getId() + " was updated");
        code += responseEntity.getBody();
    }

    public void deleteUser(Long id) {
        ResponseEntity<String> responseEntity = restTemplate.exchange(URL + "/" + id
                , HttpMethod.DELETE
                , new HttpEntity<>(headers)
                , String.class);
        System.out.println("User with ID " + id + " was deleted from DB");
        code += responseEntity.getBody();
    }

    public String getCode() {
        return code;
    }
}
