document.addEventListener("DOMContentLoaded", function() {

    let queuedFiles = [];
    let prevImgTarget = null;  // 이전에 마우스가 올라간 타겟을 추적하기 위한 변수

    const imgPreviewBoxContainer = document.getElementById("image-preview-box-container");

    var imageContainer = document.getElementById("image-container");

    const colorRadioBtn = document.querySelectorAll(".color_radio-btn_label");

    var productSubmit = document.getElementById("productSubmit");

    var imagePreview = document.getElementById("image-preview");

    var imagePreviewExpand = document.getElementById("imagePreviewExpand");

    
    // 이미지 업로드 시작 처리 관련 로직 =========================================

    
    imgPreviewBoxContainer.addEventListener("dragover", function(event) {
      event.preventDefault();
    });

    
    imgPreviewBoxContainer.addEventListener("drop", function(event) {
      event.preventDefault();
      const files = event.dataTransfer?.files;
      
      if (files && files.length > 0) {
        const fileList = [...files].sort((a, b) => a.name.localeCompare(b.name));
        handleUpdate(fileList);
      }
    });



    // 클릭 이벤트 설정 (imagePreviewBoxContainer)
    imgPreviewBoxContainer.addEventListener("click", function(event) {
      event.preventDefault();

      // 'X' 아이콘을 클릭했을 경우
      if (event.target && event.target.parentElement.className === "delete-icon") {
        const parentLi = event.target.closest('.image-preview-box');
        
        if (parentLi) {
          const index = Array.from(imgPreviewBoxContainer.children).indexOf(parentLi);
          
          if (index > -1) {
            // 해당 이미지를 queuedFiles 배열에서도 삭제
            queuedFiles.splice(index, 1);
            // 해당 이미지를 DOM에서 삭제
            parentLi.remove();
          }
        }
        return;
      }


      // 클릭 이미지 첨부 로직
      const fileInput = document.getElementById("fileInput");
      fileInput.click(); // 파일 입력을 클릭.
    });



    // 파일 입력 변경 이벤트 설정
    document.getElementById("fileInput").addEventListener("change", function(event) {
      const files = event.target.files;
      if (files && files.length > 0) {
        console.log("File input operation.");
        const fileList = [...files].sort((a, b) => a.name.localeCompare(b.name));
        handleUpdate(fileList);
      }
    });



    // 이미지 처리 관련 로직 ====================================================
    

    let isFirstUpload = true;  // 첫 번째 이미지 첨부를 추적하기 위한 플래그 변수

    async function handleUpdate(fileList) {
      const imgPreviewBoxContainer = document.getElementById("image-preview-box-container");
      queuedFiles = [...queuedFiles, ...Array.from(fileList)];

      const loadFile = (file) => new Promise((resolve, reject) => {
        const reader = new FileReader();
        
        reader.addEventListener("load", (event) => {
          resolve({ file, result: event.target.result });
        });

        reader.addEventListener("error", reject);
        reader.readAsDataURL(file);
      });
      
      const filePromises = fileList.map(loadFile);
      const loadedFiles = await Promise.all(filePromises);
      
      loadedFiles.forEach((loadedFile, index) => {

        const imgPreview = document.createElement("img");
        imgPreview.className = "image-preview";
        imgPreview.src = loadedFile.result;
        imgPreview.source = "internal";

        const imgPreviewBox = document.createElement("li");
        imgPreviewBox.className = "image-preview-box";

        const deleteIcon = document.createElement("span");
        deleteIcon.className = "delete-icon";

        const deleteImg = document.createElement("img");
        deleteImg.src = "/images/product/iconmonstr-x-mark-4.svg";
        deleteImg.alt = "Delete";
        deleteImg.className = "delete-img";

        const deleteBackground = document.createElement("div");
        deleteBackground.className = "delete-background";


        deleteIcon.appendChild(deleteImg);
        deleteIcon.appendChild(deleteBackground);
        imgPreviewBox.appendChild(deleteIcon);
        imgPreviewBox.appendChild(imgPreview);
        imgPreviewBoxContainer.append(imgPreviewBox);
    
      });

      // 첫번째 이미지 업로드 시에만 실행되는 로직.
      if (isFirstUpload) {
        isFirstUpload = false;
        imagePreviewExpandInit();
      }
      
      console.log(queuedFiles);
      
    }



    // 이미지 순서변경 로직 ======================================================



    function moveElement(arr, oldIndex, newIndex) {
      const [movedElement] = arr.splice(oldIndex, 1);
      arr.splice(newIndex, 0, movedElement);
    }

    new Sortable(imgPreviewBoxContainer, {
      animation: 150,
      filter: ".imgPreviewBox",
      preventOnFilter: false,

      onStart: function(evt) {
      },
      onEnd: function(evt) { 
      },
      onAdd: function(evt) {
      },
      onUpdate: function(evt) {
        moveElement(queuedFiles, evt.oldIndex, evt.newIndex);
        console.log(queuedFiles);
      }
    });



    // 폼 제출 ===================================================================
    document.getElementById("product-form").addEventListener("submit", async function(event) {
      event.preventDefault();
  
      const isProductCategoryName = document.getElementById("productCategoryName").value.trim();
      const isProductBrandName = document.getElementById("productBrandName").value.trim();
      const isProductName = document.getElementById("productName").value.trim();
      const isProductSize = document.getElementById("productSize").value.trim();
      const isProductPrice = document.getElementById("productPrice").value.trim();
      const isProductStock = document.getElementById("productStock").value.trim();
      const isProductDescription = document.getElementById("productDescription").value.trim();

      if (!isProductCategoryName || !isProductBrandName || !isProductName || 
        !isProductSize || !isProductPrice || !isProductStock || !isProductDescription) {
        alert("모든 필드를 채워주세요.");
        return;
      }

      const formData = new FormData(event.target);

      formData.delete("fileInput");


      let productPrice = formData.get("productPrice");

      if (productPrice) {
        productPrice = productPrice.replace(/[^0-9]/g, "");
        formData.set("productPrice", productPrice);
      }

      queuedFiles.forEach((file, index) => {
        formData.append("productImages", file);
      });
  
      const response = await fetch(event.target.action, {
        method: "POST",
        body: formData
      });
  
      if (response.ok) {
        const data = await response.json();
        if (data.status === "success"){
          console.log("Data sent successfully");
          window.location.href = data.redirectUrl;
        }
      } else {
        console.log("Error sending data");
      }
    });


    // 이미지 미리보기 큰 화면 ===================================================

    function imagePreviewExpandInit(){
      const imgPreviewInit = document.querySelector(".image-preview");
      const previewImageSrcInit = imgPreviewInit.getAttribute("src");

      imagePreviewExpand.src = previewImageSrcInit;
      imagePreviewExpand.className = "imagePreviewExpand" 

      imgPreviewInit.classList.add("--hover2");
      prevImgTarget = imgPreviewInit;
    }

    imgPreviewBoxContainer.addEventListener("mouseover", (event) => {
      event.preventDefault();
      
      if (event.target.source === "internal"){
        const previewImageSrc = event.target.getAttribute("src");

        if (imagePreviewExpand.src !== previewImageSrc) {    
          imagePreviewExpand.src = previewImageSrc;
          imagePreviewExpand.className = "imagePreviewExpand" 

          if (prevImgTarget != null) {
            prevImgTarget.classList.remove("--hover2");
          }

          event.target.classList.add("--hover2");

          prevImgTarget = event.target;
        }
      } 
    });


    // 기타 로직 ================================================================

    // imageContainer 위로 마우스가 올라가면 작동
    imageContainer.addEventListener("mouseover", (event) => {
      event.preventDefault();
      if (imgPreviewBoxContainer.childElementCount === 0) {
        imageContainer?.classList.add("--hover");
      }
    });

    // imageContainer 밖으로 마우스가 나가면 작동
    imageContainer.addEventListener("mouseout", (event) => {
      event.preventDefault();
      imageContainer?.classList.remove("--hover");
    });

    colorRadioBtn.forEach((colorRadioBtn) => {
      // 마우스가 위로 올라가면 작동
      colorRadioBtn.addEventListener("mouseover", (event) => {
        event.preventDefault();
        colorRadioBtn.classList.add("--hover");
      });
    
      // 마우스가 밖으로 나가면 작동
      colorRadioBtn.addEventListener("mouseout", (event) => {
        event.preventDefault();
        colorRadioBtn.classList.remove("--hover");
      });
    });

    // productSubmit 위로 마우스가 올라가면 작동
    productSubmit.addEventListener("mouseover", (event) => {
      event.preventDefault();
      productSubmit?.classList.add("--hover");
    });

    // productSubmit 밖으로 마우스가 나가면 작동
    productSubmit.addEventListener("mouseout", (event) => {
      event.preventDefault();
      productSubmit?.classList.remove("--hover");
    });


    $(document).on("keydown", "input:text[inputNumbersOnly]", function(event) {
      // const strVal = $(this).val();
      const key = event.key;
      const allowedKeys = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", 
      "ArrowLeft", "ArrowRight", "Backspace", "Delete", "Tab"];
      
      if (!allowedKeys.includes(key)) {
          event.preventDefault();
      }
    });
    
    $(document).on("focusout", "input:text[koreanCurrency]", function()	{
      $(this).val( $(this).val().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,") );
      // 만약 값이 있다면, 
      if($(this).val() != "" ) {
        // 원화(￦) 표시.
        $(this).val( $(this).val()+"￦");
      }		
    });
    
    $(document).on("focus", "input:text[koreanCurrency]", function()	{	
      $(this).val( $(this).val().replace("￦", ""));
    });



    // 자동완성 ========================================================================


    // 카테고리
    document.getElementById("productCategoryName").addEventListener("focus", function(event) {
      setupAutocomplete("productCategoryName", "/categoryName", "autocomplete_div__category", "autocomplete_ul__category");
    });
    // 브랜드
    document.getElementById("productBrandName").addEventListener("focus", function(event) {
      setupAutocomplete("productBrandName", "/brandName", "autocomplete_div__brand", "autocomplete_ul__brand");
    });



    // 자동완성 로직
    function setupAutocomplete(inputElementId, ajaxEndpoint, divElementId, ulElementId) {
      let suggestions = [];
      let currentFocus;
      const autocompleteDiv = document.getElementById(divElementId);
      const ulElement = document.getElementById(ulElementId);
      const inputElement = document.getElementById(inputElementId);
  
      $(inputElement).keyup(function(e) {
        let keyword = $(this).val();
        const ignoredKeys = ["ArrowUp", "ArrowDown"];
        
        if (ignoredKeys.includes(e.key)) {
          return;
        }

        if (keyword.length >= 1) {
          $.get(ajaxEndpoint, { keyword: keyword })
          .done(function(data) {
            suggestions = data.map(item => item[inputElementId]);
            updateAutocompleteList(suggestions);
          })
          .fail(function(jqXHR, textStatus, errorThrown) {
            console.error("Error: ", textStatus, errorThrown);
          });
        } else if (keyword.length < 1) {
          autocompleteDiv.classList.add("--displayFalse");
        }
      });
  
      function updateAutocompleteList(suggestions) {
        ulElement.innerHTML = "";
        autocompleteDiv.innerHTML = "";
        autocompleteDiv.classList.remove("--displayFalse");
        currentFocus = 0;
        let AddAnouncement;

        if (ulElement.id.includes("category")) {
          AddAnouncement = "새로운 카테고리를 추가합니다.";
        } else if (ulElement.id.includes("brand")) {
          AddAnouncement = "새로운 브랜드를 추가합니다.";
        }
        if (suggestions.length === 0) {
          const liElement = document.createElement("li");
          liElement.className = "autocomplete-text-li";
          liElement.textContent = `${AddAnouncement}`;
          ulElement.appendChild(liElement);
        } else {
          for (let i = 0; i < suggestions.length; i++) {
            const liElement = document.createElement("li");
            liElement.className = "autocomplete-text-li";
            liElement.textContent = suggestions[i];
            ulElement.appendChild(liElement);
            if (i === 0) {
              liElement.classList.add("autocomplete-active");
            }
          }
        }
        autocompleteDiv.appendChild(ulElement);
      }
  
      $(inputElement).keydown(function(e) {
        let items = ulElement.getElementsByTagName("li");
        if (suggestions.length >= 1) {
          if (e.keyCode === 40) {
            currentFocus++;
            addActive(items);
          } else if (e.keyCode === 38) {
            currentFocus--;
            addActive(items);
          } else if (e.keyCode === 13) {
            e.preventDefault();
            inputElement.value = items[currentFocus].textContent;
            inputElement.blur();
          }
        }
      });
  
      function addActive(items) {
        if (!items) return false;
        removeActive(items);
        if (currentFocus >= items.length) currentFocus = 0;
        if (currentFocus < 0) currentFocus = (items.length - 1);
        items[currentFocus].classList.add("autocomplete-active");
      }
  
      function removeActive(items) {
        for (let i = 0; i < items.length; i++) {
          items[i].classList.remove("autocomplete-active");
        }
      }
  
      $(inputElement).focusout(function() {
        autocompleteDiv.classList.add("--displayFalse");
      });
    }

  


});





