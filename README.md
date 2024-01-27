Copy the class into your project and you're good to go!
A lot of other cool functionality can be added to this but I was really only interested in pulling images from websites.

# How to Render Images

```java
try {
  URL url = URI.create("url"); // replace with the https url to your image

  MinecraftImage.DisplaySpecifications specs = new MinecraftImage.DisplaySpecifications(
    new Vector(0.1, 0.1, 0.1), /*x and y args represent spacing between each row particle and y arg represents spacing between each column particle*/
    100, /*the quality of each row (should range from 1 to image width)*/
    100 /*the quality of each column (should range from 1 to image height)*/
  );
  MinecraftImage img = MinecraftImage.fromURL(url, specs);
  img.display(location);
} catch (IOException e) {
  // handle exception
}
```

You can also render a gif image into a list of MinecraftImages using:
```java
MinecraftImage img = MinecraftImage.fromGIFURL("url", specs);
```
