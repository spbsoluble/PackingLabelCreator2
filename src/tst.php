<?php 
	ini_set('display_errors', 1);
	//error_reporting(E_ALL | E_STRICT);
	
	include_once 'dbc.php';
	include_once 'campaignFunctions.php';
	include_once 'callerFormFunctions.php';
	include_once 'myAccountFunctions.php';
	
	page_protect();
	
?>
<html>
<head>
<script src = "js/callerform.js" type = "text/javascript"></script>
</head>

</script>
<title>Disposition Log</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<link href="styles.css" rel="stylesheet" type="text/css">
</head>

<body>

<?php 
	if(checkAdmin()){
	echo "POST : "; 
	print_r($_POST);
	echo "<br>";

	echo "GET: ";
	print_r($_GET);
	echo "<br>";

	echo "SESSION: ";
	print_r($_SESSION);
	echo "<br>";
}
/*********************** MYACCOUNT MENU ****************************
This code shows my account menu only to logged in users. 
Copy this code till END and place it in a new html or php where
you want to show myaccount options. This is only visible to logged in users
*******************************************************************/
if (isset($_SESSION['user_id'])) {?>
<div class = "leftNav">
	<?php echo generateLeftNav(); ?>
</div>
<?php } ?>
    
      
<div class="formContainer"> 
	<div class = "titleLine">
		<h3 class="titlehdr">Add Customer <?php echo $_SESSION['user_name'];?></h3>  
		<?php	
			if (isset($_GET['msg'])){
				echo "<div class=\"error\">$_GET[msg]</div>";
			}
		?>
	</div>
  <form action="addCustomer.php" method="post" name="dispositionForm" id="dispositionForm" onSubmit="">
    <div class = "callerFormHeaders">
		<div class = "campaignIDDiv">
			<b><h2 align="right">New Customer<br/></h2></b>
		</div>
		<div class = "contactInfoHeader">
			<h2>Contact Information</h2>
		</div>
	</div>
	
	<div id = "hiddenDivs" class = "hiddenDivs">
		<div id = "customerRegContainer" class="customerRegContainer">
			<table id="callRegTable" class="callTable">
				<tr><td align="right" class = "label"><b>Customer Registration:</b></td><td></td></tr>
				<tr><td align="right" class = "label">Contact Name:</td><td> <input type="text" name="cr_fName" id="fName" value=""/></td></tr>
				<tr><td align="right" class = "label">Position:</td><td> <input type="text" name="cr_position" id="position"  value="" /></td></tr>
				<!--<tr><td align="right" class = "label">Last Name:</td><td> <input type="text" name="cr_lName" id="lName"  value="" /></td></tr> !-->
				<tr><td align="right" class = "label">Contact Email:</td><td> <input type="text" name="cr_emailAddress" id="emailAddress"  value=""/></td></tr>
				<tr><td align="right" class = "label">Company:</td><td> <input type="text" name="cr_company" id="company"  value="" /></td></tr>
				<tr><td align="right" class = "label">Company Email:</td><td> <input type="text" name="cr_companyEmailAddress" id="companyEmailAddress"  value=""/></td></tr>
				
				<tr><td align="right" class = "label">Street Address:</td><td> <input type="text" name="cr_address1" id="address1"  value=""/></td></tr>
				<!-- <tr><td align="right" class = "label">Secondary Address:</td><td> <input type="text" name="cr_address2" id="address2"  value=""/></td></tr> !-->
				<tr><td align="right" class = "label">City:</td><td> <input type="text" name="cr_city" id="city"  value=""/></td></tr>
				<tr><td align="right" class = "label">State:</td><td> <input type="text" name="cr_state" id="state"  value=""/></td></tr>
				<tr><td align="right" class = "label">Zip:</td><td> <input type="text" name="cr_zip" id="zip"  value=""/></td></tr>
				<tr><td align="right" class = "label">Primary Phone Number:</td><td> <input type="text" name="cr_phone" id="phone"  value="" /></td></tr>
				<tr><td align="right" class = "label">Secondary Phone Number:</td><td> <input type="text" name="cr_phone2" id="phone2" onClick="javascript:getIt(this)"/></td></tr>
			</table>
		</div>
	</div>
	<div class = "submitButtonsContainer">
		<input type="submit" name="doSubmit" value="Submit" /><input type="reset" value="Clear Form" />
	</div>
  </form>  
</div>
</body>
</html>
