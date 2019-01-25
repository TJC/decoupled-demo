function uploadCompleted(ev) {
  var ourBtn = $("#create-new-form").find(".btn-primary");
  ourBtn.attr("disabled", false);
  if (ev.status == 200) {
    $("#create-new-form").hide();
    alert("success!");
  } else {
    alert("Error " + ev.status + " occurred");
  }

  return true;
}

function uploadItem(event) {
  event.preventDefault();
  var ourForm = $("#new-item-form");
  var ourBtn = $("#create-new-form").find(".btn-primary");
  ourBtn.attr("disabled", true);

  // None of these work to take the form data directly from the form, and
  // I don't know why.
  // var formData = new FormData(this);
  // var formData = new FormData(ourForm[0]);
  // var formData = new FormData(document.getElementById("new-item-form"));

  var formData = new FormData();

  var titleVal = ourForm.find("input#title").val();
  if (titleVal != undefined) {
    formData.append("title", titleVal);
  }

  var descrVal = ourForm.find("textarea#description").val();
  if (descrVal != undefined) {
    formData.append("description", descrVal);
  }

  var file = ourForm.find("input#imageFile")[0].files[0];
  if (file != undefined) {
    formData.append("imageFile", file);
  }

  var xhr = new XMLHttpRequest();
  xhr.open('POST', "/item", true);
  xhr.onload = uploadCompleted;
  xhr.send(formData);

  event.preventDefault();
}

$(document).ready(function() {
  $("#create-new-form").hide();
  $("#create-new-btn").click(function() {
    $("#create-new-form").show();
  });

  $("#create-new-form form").submit(uploadItem);
})
