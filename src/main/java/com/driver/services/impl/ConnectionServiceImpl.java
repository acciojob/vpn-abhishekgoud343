package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception {
        User user = userRepository2.findById(userId).get();

        if (user.getConnected())
            throw new Exception("Already connected");

        if (user.getCountry().getCountryName().name().equalsIgnoreCase(countryName))
            return user;

        List<ServiceProvider> serviceProviderList = user.getServiceProviderList();
        serviceProviderList.sort(Comparator.comparingInt(ServiceProvider::getId));
        for (ServiceProvider serviceProvider : serviceProviderList)
            for (Country country : serviceProvider.getCountryList())
                if (country.getCountryName().name().equalsIgnoreCase(countryName)) {
                    Connection connection = new Connection();
                    connection.setServiceProvider(serviceProvider);
                    connection.setUser(user);

                    serviceProvider.getConnectionList().add(connection);

                    user.getConnectionList().add(connection);
                    user.setConnected(true);
                    user.setMaskedIp(country.getCode() + "." + serviceProvider.getId() + "." + user.getId());
                    break;
                }

        if (!user.getConnected())
            throw new Exception("Unable to connect");

        return user;
    }

    @Override
    public User disconnect(int userId) throws Exception {
        User user = userRepository2.findById(userId).get();

        if (!user.getConnected())
            throw new Exception("Already disconnected");

        user.setConnected(false);
        user.setMaskedIp(null);

        return userRepository2.save(user);
    }

    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User receiver = userRepository2.findById(receiverId).get();
        User sender = connect(senderId, receiver.getCountry().getCountryName().name());

        if (!sender.getCountry().getCode().equalsIgnoreCase(receiver.getCountry().getCode()))
            throw new Exception("Cannot establish communication");

        return sender;
    }
}