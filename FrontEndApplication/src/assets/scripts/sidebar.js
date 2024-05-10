document.addEventListener("DOMContentLoaded", () => {
  const hamBurger = document.querySelector(".toggle-btn");

  hamBurger.addEventListener("click", function () {
    document.querySelector("#sidebar").classList.toggle("expand");
    if (document.querySelector("#sidebar").classList.contains("expand")) {
      document.getElementsByClassName("main")[0].classList.add("main-expand");
      document
        .getElementsByClassName("main")[0]
        .classList.remove("main-not-expand");
    } else {
      document
        .getElementsByClassName("main")[0]
        .classList.add("main-not-expand");
      document
        .getElementsByClassName("main")[0]
        .classList.remove("main-expand");
    }
  });
});
