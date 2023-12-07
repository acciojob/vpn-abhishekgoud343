package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception {
        User user = new User();
        user.setUserName(username);
        user.setPassword(password);

        Country country = new Country();
        for (CountryName cn : CountryName.values())
            if (cn.name().equalsIgnoreCase(countryName)) {
                country.setCountryName(cn);
                country.setCode(cn.toCode());
            }

        if (country.getCountryName() == null)
            throw new Exception("Country not found");

        user.setCountry(country);

        country.setUser(user);
        countryRepository3.save(country);

        user.setOriginalIp(country.getCode() + "." + user.getId());

        return userRepository3.save(user);
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        User user = userRepository3.findById(userId).get();

        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();
        serviceProvider.getUsers().add(user);

        user.getServiceProviderList().add(serviceProvider);

        return userRepository3.save(user);
    }
}