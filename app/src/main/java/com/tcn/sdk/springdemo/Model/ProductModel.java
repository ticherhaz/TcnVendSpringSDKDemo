package com.tcn.sdk.springdemo.Model;

public class ProductModel {

    public int Id;
    public String FranID;
    public String MachineID;
    public String Name;
    public int Item_Number;
    public String Description;
    public String Type;
    public String Sub_Type;
    public String Size;
    public double Price;
    public int Quantity;
    public String Image;
    public String Entry_Date;
    public String Serial_Port_Code;
    public String Serial_Port;
    public int Temperature;
    public double RRP_Percent;
    public int PID;
    public int position;


    public ProductModel() {
    }

    public ProductModel(int id, String franID, String machineID, String name, int item_Number, String description, String type, String sub_Type, String size, double price, int quantity, String image, String entry_Date, String serial_Port_Code, String serial_Port, int temperature, double RRP_Percent, int PID) {
        Id = id;
        FranID = franID;
        MachineID = machineID;
        Name = name;
        Item_Number = item_Number;
        Description = description;
        Type = type;
        Sub_Type = sub_Type;
        Size = size;
        Price = price;
        Quantity = quantity;
        Image = image;
        Entry_Date = entry_Date;
        Serial_Port_Code = serial_Port_Code;
        Serial_Port = serial_Port;
        Temperature = temperature;
        this.RRP_Percent = RRP_Percent;
        this.PID = PID;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPID() {
        return PID;
    }

    public void setPID(int PID) {
        this.PID = PID;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getFranID() {
        return FranID;
    }

    public void setFranID(String franID) {
        FranID = franID;
    }

    public String getMachineID() {
        return MachineID;
    }

    public void setMachineID(String machineID) {
        MachineID = machineID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getItem_Number() {
        return Item_Number;
    }

    public void setItem_Number(int item_Number) {
        Item_Number = item_Number;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getSub_Type() {
        return Sub_Type;
    }

    public void setSub_Type(String sub_Type) {
        Sub_Type = sub_Type;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getEntry_Date() {
        return Entry_Date;
    }

    public void setEntry_Date(String entry_Date) {
        Entry_Date = entry_Date;
    }

    public String getSerial_Port_Code() {
        return Serial_Port_Code;
    }

    public void setSerial_Port_Code(String serial_Port_Code) {
        Serial_Port_Code = serial_Port_Code;
    }

    public String getSerial_Port() {
        return Serial_Port;
    }

    public void setSerial_Port(String serial_Port) {
        Serial_Port = serial_Port;
    }

    public int getTemperature() {
        return Temperature;
    }

    public void setTemperature(int temperature) {
        Temperature = temperature;
    }

    public double getRRP_Percent() {
        return RRP_Percent;
    }

    public void setRRP_Percent(double RRP_Percent) {
        this.RRP_Percent = RRP_Percent;
    }
}
