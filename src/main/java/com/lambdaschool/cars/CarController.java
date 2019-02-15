package com.lambdaschool.cars;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j // logging
@RestController

@RequestMapping("/cars")
public class CarController
{
    // fields
    private final CarRepository carrepos;
    private final RabbitTemplate rt;

    public CarController(CarRepository carrepos, RabbitTemplate rt)
    {
        this.carrepos = carrepos;
        this.rt = rt;
    }

    // GET --------------------------------------------------------------------------
    // Returns the Car based off of id
    @GetMapping("/id/{id}")
    public Car getById(@PathVariable Long id)
    {
        return carrepos.findById(id).orElseThrow();
    }

    // Returns a list of Cars of that year model
    @GetMapping("/year/{year}")
    public List<Car> getByYear(@PathVariable int year)
    {
        return carrepos.findAll().stream().filter(c -> c.getYear() == year)
                .collect(Collectors.toList());
    }

    // Returns a list of Cars of that brand
    @GetMapping("/brand/{brand}")
    public List<Car> getByBrand(@PathVariable String brand)
    {
        CarLog message = new CarLog("Search for brand: " +  brand);
        log.info("Search for brand: " + brand);
        rt.convertAndSend(CarsApplication.QUEUE_NAME, message.toString());
        log.info("Message Sent - Brands Search Complete");

        return carrepos.findAll().stream().filter(c -> c.getBrand().equalsIgnoreCase(brand))
                .collect(Collectors.toList());
    }

    // POST --------------------------------------------------------------------------
    // Loads multiple sets of data from the ReqBody
    @PostMapping("/upload")
    public List<Car> uploadCars(@RequestBody List<Car> newCars)
    {
        CarLog message = new CarLog("Data Loaded");
        rt.convertAndSend(CarsApplication.QUEUE_NAME, message.toString());
        log.info("Data Loaded");

        return carrepos.saveAll(newCars);
    }

    // DELETE --------------------------------------------------------------------------
    // Deletes a Car from the list based off of the id
    @DeleteMapping("/delete/{id}")
    public Car deleteCar(@PathVariable Long id)
    {
        Car car = carrepos.findById(id).orElseThrow();
        carrepos.delete(car);
        CarLog message = new CarLog("{" + id + "} Data Deleted");
        rt.convertAndSend(CarsApplication.QUEUE_NAME, message.toString());
        log.info("{" + id + "} Data Deleted");

        return car;
    }
}
