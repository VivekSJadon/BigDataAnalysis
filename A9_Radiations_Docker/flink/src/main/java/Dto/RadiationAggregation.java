package Dto;

public class RadiationAggregation {
    private String continent;
    private double totalRadiation;
    private double averageRadiation;

    public RadiationAggregation() {
    }

    public RadiationAggregation(String continent, double totalRadiation, double averageRadiation) {
        this.continent = continent;
        this.totalRadiation = totalRadiation;
        this.averageRadiation = averageRadiation;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public double getTotalRadiation() {
        return totalRadiation;
    }

    public void setTotalRadiation(double totalRadiation) {
        this.totalRadiation = totalRadiation;
    }

    public double getAverageRadiation() {
        return averageRadiation;
    }

    public void setAverageRadiation(double averageRadiation) {
        this.averageRadiation = averageRadiation;
    }

    @Override
    public String toString() {
        return "RadiationAggregation{" +
                "continent='" + continent + '\'' +
                ", totalRadiation=" + totalRadiation +
                ", averageRadiation=" + averageRadiation +
                '}';
    }
}
