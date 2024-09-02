package generators;

import com.github.javafaker.Faker;
import domain.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomerGenerator implements ObjectGenerator<Customer> {

    @Override
    public Customer generate() {
        Faker faker = new Faker();
        String name = faker.name().firstName();
        String email = name + "@" + faker.internet().domainName();
        int age = faker.number().numberBetween(16, 99);
        List<String> availableGenders = List.of("MALE", "FEMALE", "DIFFERENT", "NONE_OF_YOUR_BUSINESS");
        String gender = availableGenders.get(new Random().nextInt(availableGenders.size()));
        return new Customer(name, email, age, gender);
    }

    @Override
    public List<Customer> generateList(int size) {
        var customers = new ArrayList<Customer>();
        for (int i = 0; i < size; i++) {
            customers.add(this.generate());
        }
        return customers.stream().toList();
    }
}
