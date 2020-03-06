package com.cucumberFramework.pageObjects;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.cucumberFramework.helper.WaitHelper;

public class LoginLogoutPage {
	
	private WebDriver driver;
	
	@FindBy(xpath="//input[@type='email']")
	public WebElement userName;
	
	@FindBy(xpath="//*[@id=\"identifierNext\"]")
	WebElement Continue;
	
	@FindBy(xpath="//*[@id=\"passwordNext\"]")
	WebElement next;
	
	
	
	@FindBy(xpath="//input[@type='password']")
	public WebElement password;
	
	@FindBy(xpath="//input[@id='signInSubmit']")
	WebElement loginButton;
	
	@FindBy(xpath="//*[@id='gb_70']")
	WebElement SignInfromNav;
	
	@FindBy(xpath="//span[contains(text(),'Sign')]/parent::a")
	public WebElement logoutBtn;
	
 	WaitHelper waitHelper;
	
	public LoginLogoutPage(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
		waitHelper = new WaitHelper(driver);
	}
	
	public void enterUserName(String userName){
		this.userName.sendKeys(userName);
	}
	
	public void enterPassword(String password){
		this.password.sendKeys(password);
	}
	
	public void clickLoginButton(){
		next.click();
	}
	
	public void clickSignInButton(){
		Actions builder = new Actions(driver);
		builder.moveToElement(SignInfromNav).build().perform();
		SignInfromNav.click();
	}
	
	
	public void clickContinueButton(){
		Continue.click();
	}
	
	
	
	public void clickLogoutButton(){
		Actions builder = new Actions(driver);
		builder.moveToElement(SignInfromNav).build().perform();
		JavascriptExecutor js = (JavascriptExecutor)driver;
		 js.executeScript("arguments[0].click();", logoutBtn);;
	}
}
