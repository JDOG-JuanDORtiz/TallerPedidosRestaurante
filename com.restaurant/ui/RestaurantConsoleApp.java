package ui;

import application.CustomerService;
import application.MenuService;
import application.OrderService;
import application.ReportService;
import domain.decorator.ExtraToppingDecorator;
import domain.decorator.SideItemDecorator;
import domain.model.Customer;
import domain.model.MenuItem;
import domain.model.Order;
import domain.service.discount.FixedDiscount;
import domain.service.discount.NoDiscount;
import domain.service.discount.PercentageDiscount;
import infrastructure.notification.ConsoleNotifier;
import infrastructure.notification.EmailNotifier;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RestaurantConsoleApp {
    private final Scanner scanner;
    private final MenuService menuService;
    private final OrderService orderService;
    private final CustomerService customerService;
    private final ReportService reportService;
    private boolean running;

    public RestaurantConsoleApp(MenuService menuService, OrderService orderService,
                               CustomerService customerService, ReportService reportService) {
        this.scanner = new Scanner(System.in);
        this.menuService = menuService;
        this.orderService = orderService;
        this.customerService = customerService;
        this.reportService = reportService;
        this.running = true;
    }

    public void start() {
        System.out.println("=================================================");
        System.out.println("WELCOME TO RESTAURANT ORDER MANAGEMENT SYSTEM");
        System.out.println("=================================================");
        
        // Add some sample data
        initializeSampleData();
        
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");
            processMainMenuChoice(choice);
        }
    }

    private void displayMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Manage Menu");
        System.out.println("2. Manage Orders");
        System.out.println("3. Manage Customers");
        System.out.println("4. Reports");
        System.out.println("0. Exit");
    }

    private void processMainMenuChoice(int choice) {
        switch (choice) {
            case 1:
                menuManagementMenu();
                break;
            case 2:
                orderManagementMenu();
                break;
            case 3:
                customerManagementMenu();
                break;
            case 4:
                reportsMenu();
                break;
            case 0:
                running = false;
                System.out.println("Thank you for using the Restaurant Order Management System!");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    // ===== MENU MANAGEMENT =====
    private void menuManagementMenu() {
        boolean subMenuRunning = true;
        while (subMenuRunning) {
            System.out.println("\n=== MENU MANAGEMENT ===");
            System.out.println("1. View All Menu Items");
            System.out.println("2. Add New Menu Item");
            System.out.println("3. Edit Menu Item");
            System.out.println("4. Remove Menu Item");
            System.out.println("0. Back to Main Menu");
            
            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1:
                    displayAllMenuItems();
                    break;
                case 2:
                    addNewMenuItem();
                    break;
                case 3:
                    editMenuItem();
                    break;
                case 4:
                    removeMenuItem();
                    break;
                case 0:
                    subMenuRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayAllMenuItems() {
        List<MenuItem> menuItems = menuService.getAllMenuItems();
        if (menuItems.isEmpty()) {
            System.out.println("No menu items found.");
            return;
        }
        
        System.out.println("\n=== MENU ITEMS ===");
        Map<String, List<MenuItem>> itemsByCategory = new HashMap<>();
        
        // Group items by category
        for (MenuItem item : menuItems) {
            String category = item.getCategory();
            if (!itemsByCategory.containsKey(category)) {
                itemsByCategory.put(category, new ArrayList<>());
            }
            itemsByCategory.get(category).add(item);
        }
        
        // Display items by category
        for (String category : itemsByCategory.keySet()) {
            System.out.println("\n" + category.toUpperCase() + ":");
            for (MenuItem item : itemsByCategory.get(category)) {
                System.out.println(item.getId().substring(0, 8) + " | " + item);
            }
        }
    }

    private void addNewMenuItem() {
        System.out.println("\n=== ADD NEW MENU ITEM ===");
        
        System.out.println("Select menu item type:");
        System.out.println("1. Main Dish");
        System.out.println("2. Appetizer");
        System.out.println("3. Beverage");
        System.out.println("4. Dessert");
        
        int typeChoice = getIntInput("Enter your choice: ");
        String type;
        String extraPropertyPrompt;
        
        switch (typeChoice) {
            case 1:
                type = "main";
                extraPropertyPrompt = "Is this dish spicy? (true/false): ";
                break;
            case 2:
                type = "appetizer";
                extraPropertyPrompt = "Is this appetizer vegetarian? (true/false): ";
                break;
            case 3:
                type = "beverage";
                extraPropertyPrompt = "Is this beverage alcoholic? (true/false): ";
                break;
            case 4:
                type = "dessert";
                extraPropertyPrompt = "Does this dessert contain nuts? (true/false): ";
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        
        String name = getStringInput("Enter item name: ");
        double price = getDoubleInput("Enter price: ");
        String description = getStringInput("Enter description: ");
        boolean extraProperty = getBooleanInput(extraPropertyPrompt);
        
        try {
            menuService.addMenuItem(type, name, price, description, extraProperty);
            System.out.println("Menu item added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding menu item: " + e.getMessage());
        }
    }

    private void editMenuItem() {
        displayAllMenuItems();
        String itemId = getStringInput("\nEnter item ID to edit: ");
        
        Optional<MenuItem> itemOpt = menuService.getMenuItemById(itemId);
        if (!itemOpt.isPresent()) {
            System.out.println("Menu item not found.");
            return;
        }
        
        MenuItem item = itemOpt.get();
        System.out.println("\nEditing: " + item);
        
        String name = getStringInput("Enter new name (or press Enter to keep current): ");
        if (!name.isEmpty()) {
            item.setName(name);
        }
        
        String priceStr = getStringInput("Enter new price (or press Enter to keep current): ");
        if (!priceStr.isEmpty()) {
            try {
                double price = Double.parseDouble(priceStr);
                item.setPrice(price);
            } catch (NumberFormatException e) {
                System.out.println("Invalid price format. Price not updated.");
            }
        }
        
        String description = getStringInput("Enter new description (or press Enter to keep current): ");
        if (!description.isEmpty()) {
            item.setDescription(description);
        }
        
        menuService.updateMenuItem(item);
        System.out.println("Menu item updated successfully!");
    }

    private void removeMenuItem() {
        displayAllMenuItems();
        String itemId = getStringInput("\nEnter item ID to remove: ");
        
        try {
            menuService.removeMenuItem(itemId);
            System.out.println("Menu item removed successfully!");
        } catch (Exception e) {
            System.out.println("Error removing menu item: " + e.getMessage());
        }
    }

    // ===== ORDER MANAGEMENT =====
    private void orderManagementMenu() {
        boolean subMenuRunning = true;
        while (subMenuRunning) {
            System.out.println("\n=== ORDER MANAGEMENT ===");
            System.out.println("1. View All Orders");
            System.out.println("2. Create New Order");
            System.out.println("3. Add Items to Order");
            System.out.println("4. Apply Discount");
            System.out.println("5. Progress Order Status");
            System.out.println("6. View Order Details");
            System.out.println("0. Back to Main Menu");
            
            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1:
                    displayAllOrders();
                    break;
                case 2:
                    createNewOrder();
                    break;
                case 3:
                    addItemsToOrder();
                    break;
                case 4:
                    applyDiscount();
                    break;
                case 5:
                    progressOrderStatus();
                    break;
                case 6:
                    viewOrderDetails();
                    break;
                case 0:
                    subMenuRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }
        
        System.out.println("\n=== ALL ORDERS ===");
        for (Order order : orders) {
            System.out.println(order.getId().substring(0, 8) + " | Customer: " + 
                order.getCustomer().getName() + " | Status: " + order.getStatus() + 
                " | Total: $" + String.format("%.2f", order.calculateTotal()));
        }
    }

    private void createNewOrder() {
        System.out.println("\n=== CREATE NEW ORDER ===");
        System.out.println("1. Existing Customer");
        System.out.println("2. New Customer");
        
        int customerChoice = getIntInput("Enter your choice: ");
        
        Order order = null;
        
        if (customerChoice == 1) {
            List<Customer> customers = customerService.getAllCustomers();
            if (customers.isEmpty()) {
                System.out.println("No customers found. Please create a new customer.");
                createNewCustomer();
                return;
            }
            
            System.out.println("\n=== EXISTING CUSTOMERS ===");
            for (Customer customer : customers) {
                System.out.println(customer.getId().substring(0, 8) + " | " + customer);
            }
            
            String customerId = getStringInput("\nEnter customer ID: ");
            try {
                order = orderService.createOrder(customerId);
            } catch (Exception e) {
                System.out.println("Error creating order: " + e.getMessage());
                return;
            }
        } else if (customerChoice == 2) {
            String name = getStringInput("Enter customer name: ");
            String address = getStringInput("Enter customer address: ");
            String phone = getStringInput("Enter customer phone: ");
            
            Customer customer = new Customer(name, address, phone);
            order = orderService.createOrder(customer);
        } else {
            System.out.println("Invalid choice.");
            return;
        }
        
        // Add observers to the order
        orderService.addOrderObserver(order.getId(), new ConsoleNotifier());
        orderService.addOrderObserver(order.getId(), new EmailNotifier());
        
        System.out.println("Order created successfully! Order ID: " + order.getId().substring(0, 8));
    }

    private void addItemsToOrder() {
        displayAllOrders();
        String orderId = getStringInput("\nEnter order ID: ");
        
        Optional<Order> orderOpt = orderService.getOrderById(orderId);
        if (!orderOpt.isPresent()) {
            System.out.println("Order not found.");
            return;
        }
        
        boolean addingItems = true;
        while (addingItems) {
            displayAllMenuItems();
            String menuItemId = getStringInput("\nEnter menu item ID to add (or 0 to finish): ");
            
            if (menuItemId.equals("0")) {
                addingItems = false;
                continue;
            }
            
            Optional<MenuItem> menuItemOpt = menuService.getMenuItemById(menuItemId);
            if (!menuItemOpt.isPresent()) {
                System.out.println("Menu item not found.");
                continue;
            }
            
            MenuItem menuItem = menuItemOpt.get();
            
            // Ask if user wants to add customizations
            System.out.println("\nDo you want to add customizations to this item?");
            System.out.println("1. Add extra topping");
            System.out.println("2. Add side item");
            System.out.println("3. No customization");
            
            int customizationChoice = getIntInput("Enter your choice: ");
            
            if (customizationChoice == 1) {
                String toppingName = getStringInput("Enter topping name: ");
                double toppingPrice = getDoubleInput("Enter topping price: ");
                menuItem = new ExtraToppingDecorator(menuItem, toppingName, toppingPrice);
            } else if (customizationChoice == 2) {
                String sideName = getStringInput("Enter side item name: ");
                double sidePrice = getDoubleInput("Enter side item price: ");
                menuItem = new SideItemDecorator(menuItem, sideName, sidePrice);
            }
            
            int quantity = getIntInput("Enter quantity: ");
            
            try {
                orderService.addItemToOrder(orderId, menuItem.getId(), quantity);
                System.out.println("Item added to order successfully!");
            } catch (Exception e) {
                System.out.println("Error adding item to order: " + e.getMessage());
            }
            
            boolean addMore = getBooleanInput("Add more items? (true/false): ");
            addingItems = addMore;
        }
    }

    private void applyDiscount() {
        displayAllOrders();
        String orderId = getStringInput("\nEnter order ID: ");
        
        Optional<Order> orderOpt = orderService.getOrderById(orderId);
        if (!orderOpt.isPresent()) {
            System.out.println("Order not found.");
            return;
        }
        
        System.out.println("\nSelect discount type:");
        System.out.println("1. Percentage Discount");
        System.out.println("2. Fixed Amount Discount");
        System.out.println("3. No Discount");
        
        int discountChoice = getIntInput("Enter your choice: ");
        
        try {
            switch (discountChoice) {
                case 1:
                    double percentage = getDoubleInput("Enter discount percentage: ");
                    orderService.applyDiscount(orderId, new PercentageDiscount(percentage));
                    System.out.println(percentage + "% discount applied to order!");
                    break;
                case 2:
                    double amount = getDoubleInput("Enter fixed discount amount: ");
                    orderService.applyDiscount(orderId, new FixedDiscount(amount));
                    System.out.println("$" + amount + " discount applied to order!");
                    break;
                case 3:
                    orderService.applyDiscount(orderId, new NoDiscount());
                    System.out.println("No discount applied to order.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (Exception e) {
            System.out.println("Error applying discount: " + e.getMessage());
        }
    }

    private void progressOrderStatus() {
        displayAllOrders();
        String orderId = getStringInput("\nEnter order ID: ");
        
        try {
            orderService.progressOrderState(orderId);
            Optional<Order> updatedOrder = orderService.getOrderById(orderId);
            if (updatedOrder.isPresent()) {
                System.out.println("Order status updated to: " + updatedOrder.get().getStatus());
            }
        } catch (Exception e) {
            System.out.println("Error updating order status: " + e.getMessage());
        }
    }

    private void viewOrderDetails() {
        displayAllOrders();
        String orderId = getStringInput("\nEnter order ID: ");
        
        Optional<Order> orderOpt = orderService.getOrderById(orderId);
        if (!orderOpt.isPresent()) {
            System.out.println("Order not found.");
            return;
        }
        
        System.out.println("\n=== ORDER DETAILS ===");
        System.out.println(orderOpt.get().toString());
    }

    // ===== CUSTOMER MANAGEMENT =====
    private void customerManagementMenu() {
        boolean subMenuRunning = true;
        while (subMenuRunning) {
            System.out.println("\n=== CUSTOMER MANAGEMENT ===");
            System.out.println("1. View All Customers");
            System.out.println("2. Add New Customer");
            System.out.println("3. Edit Customer");
            System.out.println("4. Find Customer by Phone");
            System.out.println("0. Back to Main Menu");
            
            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1:
                    displayAllCustomers();
                    break;
                case 2:
                    createNewCustomer();
                    break;
                case 3:
                    editCustomer();
                    break;
                case 4:
                    findCustomerByPhone();
                    break;
                case 0:
                    subMenuRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }
        
        System.out.println("\n=== ALL CUSTOMERS ===");
        for (Customer customer : customers) {
            System.out.println(customer.getId().substring(0, 8) + " | " + customer.getName() + 
                " | " + customer.getPhone() + " | " + customer.getAddress());
        }
    }

    private void createNewCustomer() {
        System.out.println("\n=== ADD NEW CUSTOMER ===");
        String name = getStringInput("Enter customer name: ");
        String address = getStringInput("Enter customer address: ");
        String phone = getStringInput("Enter customer phone: ");
        
        try {
            customerService.addCustomer(name, address, phone);
            System.out.println("Customer added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding customer: " + e.getMessage());
        }
    }

    private void editCustomer() {
        displayAllCustomers();
        String customerId = getStringInput("\nEnter customer ID to edit: ");
        
        Optional<Customer> customerOpt = customerService.getCustomerById(customerId);
        if (!customerOpt.isPresent()) {
            System.out.println("Customer not found.");
            return;
        }
        
        Customer customer = customerOpt.get();
        System.out.println("\nEditing: " + customer.getName());
        
        String name = getStringInput("Enter new name (or press Enter to keep current): ");
        if (!name.isEmpty()) {
            customer.setName(name);
        }
        
        String address = getStringInput("Enter new address (or press Enter to keep current): ");
        if (!address.isEmpty()) {
            customer.setAddress(address);
        }
        
        String phone = getStringInput("Enter new phone (or press Enter to keep current): ");
        if (!phone.isEmpty()) {
            customer.setPhone(phone);
        }
        
        customerService.updateCustomer(customer);
        System.out.println("Customer updated successfully!");
    }

    private void findCustomerByPhone() {
        String phone = getStringInput("Enter customer phone number: ");
        
        Optional<Customer> customerOpt = customerService.getCustomerByPhone(phone);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            System.out.println("\nCustomer found:");
            System.out.println("ID: " + customer.getId().substring(0, 8));
            System.out.println("Name: " + customer.getName());
            System.out.println("Address: " + customer.getAddress());
            System.out.println("Phone: " + customer.getPhone());
        } else {
            System.out.println("No customer found with that phone number.");
        }
    }

    // ===== REPORTS =====
    private void reportsMenu() {
        boolean subMenuRunning = true;
        while (subMenuRunning) {
            System.out.println("\n=== REPORTS ===");
            System.out.println("1. Daily Sales Report");
            System.out.println("2. Most Popular Items");
            System.out.println("3. Revenue by Category");
            System.out.println("0. Back to Main Menu");
            
            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1:
                    generateDailySalesReport();
                    break;
                case 2:
                    generateMostPopularItemsReport();
                    break;
                case 3:
                    generateRevenueByCategoryReport();
                    break;
                case 0:
                    subMenuRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void generateDailySalesReport() {
        Date date = getDateInput("Enter date (MM/dd/yyyy): ");
        if (date == null) {
            return;
        }
        
        String report = reportService.generateDailySalesReport(date);
        System.out.println(report);
    }

    private void generateMostPopularItemsReport() {
        Date date = getDateInput("Enter date (MM/dd/yyyy): ");
        if (date == null) {
            return;
        }
        
        int limit = getIntInput("Enter number of top items to display: ");
        Map<String, Integer> popularItems = reportService.getMostPopularItems(date, limit);
        
        System.out.println("\n=== MOST POPULAR ITEMS ===");
        System.out.println("Date: " + date);
        
        if (popularItems.isEmpty()) {
            System.out.println("No data available for the selected date.");
            return;
        }
        
        int rank = 1;
        for (Map.Entry<String, Integer> entry : popularItems.entrySet()) {
            System.out.println(rank + ". " + entry.getKey() + " - " + entry.getValue() + " sold");
            rank++;
        }
    }

    private void generateRevenueByCategoryReport() {
        Date date = getDateInput("Enter date (MM/dd/yyyy): ");
        if (date == null) {
            return;
        }
        
        Map<String, Double> categoryRevenue = reportService.getRevenueByCategory(date);
        
        System.out.println("\n=== REVENUE BY CATEGORY ===");
        System.out.println("Date: " + date);
        
        if (categoryRevenue.isEmpty()) {
            System.out.println("No data available for the selected date.");
            return;
        }
        
        double totalRevenue = 0;
        for (Map.Entry<String, Double> entry : categoryRevenue.entrySet()) {
            System.out.println(entry.getKey() + ": $" + String.format("%.2f", entry.getValue()));
            totalRevenue += entry.getValue();
        }
        
        System.out.println("\nTotal Revenue: $" + String.format("%.2f", totalRevenue));
    }

    // ===== HELPER METHODS =====
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    private double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    private boolean getBooleanInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("true") || input.equals("yes") || input.equals("y")) {
                return true;
            } else if (input.equals("false") || input.equals("no") || input.equals("n")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter true or false.");
            }
        }
    }
    
    private Date getDateInput(String prompt) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return dateFormat.parse(input);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use MM/dd/yyyy.");
                String retry = getStringInput("Try again? (y/n): ");
                if (!retry.equalsIgnoreCase("y")) {
                    return null;
                }
            }
        }
    }
    
    private void initializeSampleData() {
        // Add sample menu items
        menuService.addMenuItem("main", "Grilled Chicken", 15.99, "Herb marinated grilled chicken breast", true);
        menuService.addMenuItem("main", "Beef Steak", 24.99, "Premium cut beef steak", false);
        menuService.addMenuItem("main", "Vegetable Pasta", 12.99, "Pasta with seasonal vegetables", false);
        
        menuService.addMenuItem("appetizer", "Caesar Salad", 8.99, "Fresh romaine lett

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name + " - $" + String.format("%.2f", price) + " (" + description + ")";
    }
}
