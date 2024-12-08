package com.example.humayan;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class InsectResponse {

    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public static class Result {
        private Classification classification;
        private IsInsect isInsect;

        public Classification getClassification() {
            return classification;
        }

        public void setClassification(Classification classification) {
            this.classification = classification;
        }

        public IsInsect getIsInsect() {
            return isInsect;
        }

        public void setIsInsect(IsInsect isInsect) {
            this.isInsect = isInsect;
        }

        public static class Classification {
            private List<Suggestion> suggestions;

            public List<Suggestion> getSuggestions() {
                return suggestions;
            }

            public void setSuggestions(List<Suggestion> suggestions) {
                this.suggestions = suggestions;
            }

            public static class Suggestion {
                private String name; // Scientific name
                private Details details;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public Details getDetails() {
                    return details;
                }

                public void setDetails(Details details) {
                    this.details = details;
                }

                public static class Details {
                    @SerializedName("common_names")
                    private List<String> commonNames;

                    @SerializedName("description")
                    private Description description;

                    @SerializedName("image")
                    private Image image;

                    @SerializedName("url")
                    private String url;

                    @SerializedName("language")
                    private String language;

                    @SerializedName("citation")
                    private String citation;

                    @SerializedName("license_name")
                    private String licenseName;

                    @SerializedName("similar_images")
                    private List<SimilarImage> similarImages;

                    public List<String> getCommonNames() {
                        return commonNames;
                    }

                    public void setCommonNames(List<String> commonNames) {
                        this.commonNames = commonNames;
                    }

                    public Description getDescription() {
                        return description;
                    }

                    public void setDescription(Description description) {
                        this.description = description;
                    }

                    public Image getImage() {
                        return image;
                    }

                    public void setImage(Image image) {
                        this.image = image;
                    }

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }

                    public String getLanguage() {
                        return language;
                    }

                    public void setLanguage(String language) {
                        this.language = language;
                    }

                    public String getCitation() {
                        return citation;
                    }

                    public void setCitation(String citation) {
                        this.citation = citation;
                    }

                    public String getLicenseName() {
                        return licenseName;
                    }

                    public void setLicenseName(String licenseName) {
                        this.licenseName = licenseName;
                    }

                    public List<SimilarImage> getSimilarImages() {
                        return similarImages;
                    }

                    public void setSimilarImages(List<SimilarImage> similarImages) {
                        this.similarImages = similarImages;
                    }
                }

                public static class Description {
                    private String value;

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }
                }

                public static class Image {
                    private String value;

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }
                }

                public static class SimilarImage {
                    private String id;
                    private String url;
                    private Double similarity;
                    private String urlSmall;

                    public String getId() {
                        return id;
                    }

                    public void setId(String id) {
                        this.id = id;
                    }

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }

                    public Double getSimilarity() {
                        return similarity;
                    }

                    public void setSimilarity(Double similarity) {
                        this.similarity = similarity;
                    }

                    public String getUrlSmall() {
                        return urlSmall;
                    }

                    public void setUrlSmall(String urlSmall) {
                        this.urlSmall = urlSmall;
                    }
                }
            }
        }

        public static class IsInsect {
            private Double probability;
            private Double threshold;
            private Boolean binary;

            public Double getProbability() {
                return probability;
            }

            public void setProbability(Double probability) {
                this.probability = probability;
            }

            public Double getThreshold() {
                return threshold;
            }

            public void setThreshold(Double threshold) {
                this.threshold = threshold;
            }

            public Boolean getBinary() {
                return binary;
            }

            public void setBinary(Boolean binary) {
                this.binary = binary;
            }
        }
    }
}
