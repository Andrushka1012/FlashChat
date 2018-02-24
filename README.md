<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Название страницы</title>
<link href = "css/style.css" type = "text/css" rel = "stylesheet"/>
<script src="js/jquery-3.2.1.min.js"></script>
<script src="js/galleryscript.js"></script>
</head>
<body>
	<div id = "weaper">
		<div id = "header">
			<div id = "top_header">
				<ul>
					<li><a id = "logo" class = "active"href = "gallery.htm">WebName</a></li>
					<li><a href = "#">You</a></li>
					<li><a href = "#">Create</a></li>
				</ul>
				<div id = "right_header">
					<ul>
						<li><input id = "search" type = "text" placeholder = "Search photos or peoples" onfocus = "searcjOnFocuse()" onblur = "searchOnBlur()" ></input></li>
						<li><a href = "#"><img class = "circular_img" src = "img\andrushka.jpg" alt = "User Photo"/></a></li>
					</ul>
				</div>
				<div id = "search_element">
				<ul>
						<li><a href = "#">Find photos</a></li>
						<li><a href = "#">Find pepoles</a></li>
					</ul>
				</div>
			</div>
			<!-- Tab links -->
				<div class="tab">
				  <button class="tablinks active" onclick="openTab(event, 'Photos')">Photos</button>
				  <button class="tablinks" onclick="openTab(event, 'Peoples')">Peoples</button>
				</div>

			
		</div>
	<div id = "content">
				<!-- Tab content -->
				<div id="Photos" class="tabcontent" style="display: block" >
					<div class="card">
						<a id = "picrurename1" class = "picrurename" href="#">User</a>
						<h3 id = "date1" class = "publicationDate">16:46 04.01.2018</h3>
						<img id = "img1" src="img/photo.jpg" alt="Avatar" style="width:100%;height: 220px" onclick="onImageClick(1)">
						<div class="containerq">
							<h4 id = "description1"class = "description">Following up on our announcement of the Top-25
								overall images on Flickr in 2017, we wanted to give
								you a closer look at the most popular photos
								from various countries and territories like
								the United States, Germany, or Brazil.</h4>
							<p class = "line"></p>
							<a class = "moreFromThisUser" href="#">More from this user</a>
							<p style="height: 10px"></p>
						</div>
					</div>
					<div class="card">
						<a id = "picrurename2" class = "picrurename" href="#">User</a>
						<h3 id = "date2" class = "publicationDate">16:46 04.01.2018</h3>
						<img id = "img2" src="img/photo.jpg" alt="Avatar" style="width:100%;height: 220px" onclick="onImageClick(2)">
						<div class="containerq">
							<h4 id = "description2" class = "description">Following up on our announcement of the Top-25
								overall images on Flickr in 2017, we wanted to give
								you a closer look at the most popular photos
								from various countries and territories like
								the United States, Germany, or Brazil.</h4>
							<p class = "line"></p>
							<a class = "moreFromThisUser" href="#">More from this user</a>
							<p style="height: 10px"></p>
						</div>
					</div>
					<div class="card">
						<a id = "picrurename3" class = "picrurename" href="#">User</a>
						<h3 id = "date3" class = "publicationDate">16:46 04.01.2018</h3>
						<img id = "img3" src="img/photo.jpg" alt="Avatar" style="width:100%;height: 220px" onclick="onImageClick(3)">
						<div class="containerq">
							<h4 id = "description3" class = "description">Following up on our announcement of the Top-25
								overall images on Flickr in 2017, we wanted to give
								you a closer look at the most popular photos
								from various countries and territories like
								the United States, Germany, or Brazil.</h4>
							<p class = "line"></p>
							<a class = "moreFromThisUser" href="#">More from this user</a>
							<p style="height: 10px"></p>
						</div>
					</div><div class="card">
					<a id = "picrurename4" class = "picrurename" href="#">User</a>
					<h3 id = "date4" class = "publicationDate">16:46 04.01.2018</h3>
					<img id = "img4" src="img/photo.jpg" alt="Avatar" style="width:100%;height: 220px" onclick="onImageClick(4)">
					<div class="containerq">
						<h4 id = "description4" class = "description">Following up on our announcement of the Top-25
							overall images on Flickr in 2017, we wanted to give
							you a closer look at the most popular photos
							from various countries and territories like
							the United States, Germany, or Brazil.</h4>
						<p class = "line"></p>
						<a class = "moreFromThisUser" href="#">More from this user</a>
						<p style="height: 10px"></p>
					</div>
				</div>
					<div class="card">
						<a id = "picrurename5" class = "picrurename" href="#">User</a>
						<h3 id = "date5" class = "publicationDate">16:46 04.01.2018</h3>
						<img id = "img5" src="img/photo.jpg" alt="Avatar" style="width:100%;height: 220px" onclick="onImageClick(5)">
						<div class="containerq">
							<h4 id = "description5" class = "description">Following up on our announcement of the Top-25
								overall images on Flickr in 2017, we wanted to give
								you a closer look at the most popular photos
								from various countries and territories like
								the United States, Germany, or Brazil.</h4>
							<p class = "line"></p>
							<a class = "moreFromThisUser" href="#">More from this user</a>
							<p style="height: 10px"></p>
						</div>
					</div>
					<div class="card">
						<a id = "picrurename6" class = "picrurename" href="#">User</a>
						<h3 id = "date6" class = "publicationDate">16:46 04.01.2018</h3>
						<img id = "img6" src="img/photo.jpg" alt="Avatar" style="width:100%;height: 220px" onclick="onImageClick(6)">
						<div class="containerq">
							<h4 id = "description6" class = "description">Following up on our announcement of the Top-25
								overall images on Flickr in 2017, we wanted to give
								you a closer look at the most popular photos
								from various countries and territories like
								the United States, Germany, or Brazil.</h4>
							<p class = "line"></p>
							<a class = "moreFromThisUser" href="#">More from this user</a>
							<p style="height: 10px"></p>
						</div>
					</div><div class="card">
					<a  id = "picrurename7" class = "picrurename" href="#">User</a>
					<h3 id = "date7" class = "publicationDate">16:46 04.01.2018</h3>
					<img id = "img7" src="img/photo.jpg" alt="Avatar" style="width:100%;height: 220px" onclick="onImageClick(7)">
					<div class="containerq">
						<h4 id = "description7" class = "description">Following up on our announcement of the Top-25
							overall images on Flickr in 2017, we wanted to give
							you a closer look at the most popular photos
							from various countries and territories like
							the United States, Germany, or Brazil.</h4>
						<p class = "line"></p>
						<a class = "moreFromThisUser" href="#">More from this user</a>
						<p style="height: 10px"></p>
					</div>
				</div>
					<div class="card">
						<a id = "picrurename8" class = "picrurename" href="#">User</a>
						<h3 id = "date8" class = "publicationDate">16:46 04.01.2018</h3>
						<img id = "img8" src="img/photo.jpg" alt="Avatar" style="width:100%;height: 220px" onclick="onImageClick(8)">
						<div class="containerq">
							<h4 id = "description8" class = "description">Following up on our announcement of the Top-25
								overall images on Flickr in 2017, we wanted to give
								you a closer look at the most popular photos
								from various countries and territories like
								the United States, Germany, or Brazil.</h4>
							<p class = "line"></p>
							<a class = "moreFromThisUser" href="#">More from this user</a>
							<p style="height: 10px"></p>
						</div>
					</div>
					<div class="card">
						<a id = "picrurename9" class = "picrurename" href="#">User</a>
						<h3 id = "date9" class = "publicationDate">16:46 04.01.2018</h3>
						<img id = "img9" src="img/photo.jpg" alt="Avatar" style="width:100%;height: 220px" onclick="onImageClick(9)">
						<div class="containerq">
							<h4 id = "description9" class = "description">Following up on our announcement of the Top-25
								overall images on Flickr in 2017, we wanted to give
								you a closer look at the most popular photos
								from various countries and territories like
								the United States, Germany, or Brazil.</h4>
							<p class = "line"></p>
							<a class = "moreFromThisUser" href="#">More from this user</a>
							<p style="height: 10px"></p>
						</div>
					</div>
				</div>



				<div id="Peoples" class="tabcontent">
				  <p>No peoples added</p>
				</div>
			</div>

	</div>
	
</body>
</html>






FlashChat
<a href="https://imgflip.com/gif/257v1f"><img src="https://i.imgflip.com/257v1f.gif" title="made at imgflip.com"/></a>
