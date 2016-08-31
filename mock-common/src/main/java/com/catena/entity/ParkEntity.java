package com.catena.entity;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by hx-pc on 16-7-22.
 */
public class ParkEntity {

    private static String json;

    static {
        File file = new File("data/parkList.json");
        InputStreamReader isr;
        StringBuilder lineStr = new StringBuilder();
        try {
            isr = new InputStreamReader(new FileInputStream(file), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            while (true) {
                String s = br.readLine();
                if (s != null) {
                    lineStr.append(s);
                } else
                    break;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        json = lineStr.toString();
    }

    private Integer id;
    private String district;
    private String name;
    private String type1;
    private String type2;
    private String zzdw;
    private String attention;
    private String phone;
    private String area;
    private String address;
    private String level;
    private String allowInCompany;
    private String nowInCompany;
    private String allInCompany;
    private String helpCompanyNumber;
    private String employment;
    private String administrativeVillages;
    private String registeredPopulation;
    private String residentsPopulation;
    private String poorVillage;
    private String poorPeople;
    private String smartCompany;
    private String allowance;
    private String exchangeLand;
    private String xzxkz;
    private String gljlbzzzl;
    private String scx;
    private String cdzjyhl;
    private String other;
    private String has_xzxkz;
    private String has_gljlbzzzl;
    private String has_scx;
    private String has_cdzjyhl;
    private String has_other;
    private List<Integer> target;


    public static List<LinkedHashMap> buildListParkEntity() throws IOException {
        return new ObjectMapper().readValue(json.getBytes(), List.class);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public String getZzdw() {
        return zzdw;
    }

    public void setZzdw(String zzdw) {
        this.zzdw = zzdw;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAllowInCompany() {
        return allowInCompany;
    }

    public void setAllowInCompany(String allowInCompany) {
        this.allowInCompany = allowInCompany;
    }

    public String getNowInCompany() {
        return nowInCompany;
    }

    public void setNowInCompany(String nowInCompany) {
        this.nowInCompany = nowInCompany;
    }

    public String getAllInCompany() {
        return allInCompany;
    }

    public void setAllInCompany(String allInCompany) {
        this.allInCompany = allInCompany;
    }

    public String getHelpCompanyNumber() {
        return helpCompanyNumber;
    }

    public void setHelpCompanyNumber(String helpCompanyNumber) {
        this.helpCompanyNumber = helpCompanyNumber;
    }

    public String getEmployment() {
        return employment;
    }

    public void setEmployment(String employment) {
        this.employment = employment;
    }

    public String getAdministrativeVillages() {
        return administrativeVillages;
    }

    public void setAdministrativeVillages(String administrativeVillages) {
        this.administrativeVillages = administrativeVillages;
    }

    public String getRegisteredPopulation() {
        return registeredPopulation;
    }

    public void setRegisteredPopulation(String registeredPopulation) {
        this.registeredPopulation = registeredPopulation;
    }

    public String getResidentsPopulation() {
        return residentsPopulation;
    }

    public void setResidentsPopulation(String residentsPopulation) {
        this.residentsPopulation = residentsPopulation;
    }

    public String getPoorVillage() {
        return poorVillage;
    }

    public void setPoorVillage(String poorVillage) {
        this.poorVillage = poorVillage;
    }

    public String getPoorPeople() {
        return poorPeople;
    }

    public void setPoorPeople(String poorPeople) {
        this.poorPeople = poorPeople;
    }

    public String getSmartCompany() {
        return smartCompany;
    }

    public void setSmartCompany(String smartCompany) {
        this.smartCompany = smartCompany;
    }

    public String getAllowance() {
        return allowance;
    }

    public void setAllowance(String allowance) {
        this.allowance = allowance;
    }

    public String getExchangeLand() {
        return exchangeLand;
    }

    public void setExchangeLand(String exchangeLand) {
        this.exchangeLand = exchangeLand;
    }

    public String getXzxkz() {
        return xzxkz;
    }

    public void setXzxkz(String xzxkz) {
        this.xzxkz = xzxkz;
    }

    public String getGljlbzzzl() {
        return gljlbzzzl;
    }

    public void setGljlbzzzl(String gljlbzzzl) {
        this.gljlbzzzl = gljlbzzzl;
    }

    public String getScx() {
        return scx;
    }

    public void setScx(String scx) {
        this.scx = scx;
    }

    public String getCdzjyhl() {
        return cdzjyhl;
    }

    public void setCdzjyhl(String cdzjyhl) {
        this.cdzjyhl = cdzjyhl;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getHas_xzxkz() {
        return has_xzxkz;
    }

    public void setHas_xzxkz(String has_xzxkz) {
        this.has_xzxkz = has_xzxkz;
    }

    public String getHas_gljlbzzzl() {
        return has_gljlbzzzl;
    }

    public void setHas_gljlbzzzl(String has_gljlbzzzl) {
        this.has_gljlbzzzl = has_gljlbzzzl;
    }

    public String getHas_scx() {
        return has_scx;
    }

    public void setHas_scx(String has_scx) {
        this.has_scx = has_scx;
    }

    public String getHas_cdzjyhl() {
        return has_cdzjyhl;
    }

    public void setHas_cdzjyhl(String has_cdzjyhl) {
        this.has_cdzjyhl = has_cdzjyhl;
    }

    public String getHas_other() {
        return has_other;
    }

    public void setHas_other(String has_other) {
        this.has_other = has_other;
    }

    public List<Integer> getTarget() {
        return target;
    }

    public void setTarget(List<Integer> target) {
        this.target = target;
    }
}
