const puppeteer = require("puppeteer");
const cas = require("../../cas.js");

(async () => {
    let failed = false;
    try {
        const browser = await puppeteer.launch(cas.browserOptions());
        const page = await cas.newPage(browser);

        await cas.log("Trying without an exising SSO session...");
        await cas.goto(page, "https://localhost:9876/fediz");
        await cas.waitForTimeout(page, 2000);
        await cas.screenshot(page);
        await cas.waitForElement(page, "#logincas");
        await cas.click(page, "#logincas");
        await cas.waitForTimeout(page, 2000);
        await cas.screenshot(page);
        await cas.waitForElement(page, "#username");
        await cas.loginWith(page);
        await cas.waitForResponse(page);
        await cas.waitForTimeout(page, 2000);
        await cas.screenshot(page);
        await cas.logPage(page);
        await cas.assertInnerText(page, "#principalId", "casuser");
        await cas.assertVisibility(page, "#assertion");
        await cas.waitForTimeout(page, 2000);
        await cas.assertInnerText(page, "#claim0", "http://schemas.xmlsoap.org/claims/EmailAddress:casuser@example.org");
        await cas.assertInnerText(page, "#claim1", "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname:casuser");
        await cas.assertInnerText(page, "#claim2", "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress:CAS@example.org");

        await cas.log("Trying with an exising SSO session...");
        await cas.gotoLogout(page);
        await cas.gotoLogin(page);
        await cas.loginWith(page);
        await cas.waitForTimeout(page, 3000);
        await cas.assertCookie(page);
        await cas.goto(page, "https://localhost:9876/fediz");
        await cas.waitForTimeout(page, 2000);
        await cas.waitForElement(page, "#logincas");
        await cas.click(page, "#logincas");
        await cas.waitForTimeout(page, 2000);
        await cas.waitForElement(page, "#principalId");
        await cas.logPage(page);
        await cas.assertInnerText(page, "#principalId", "casuser");
        await cas.assertVisibility(page, "#assertion");
        await cas.waitForTimeout(page, 2000);
        await cas.assertInnerText(page, "#claim0", "http://schemas.xmlsoap.org/claims/EmailAddress:casuser@example.org");
        await cas.assertInnerText(page, "#claim1", "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname:casuser");
        await cas.assertInnerText(page, "#claim2", "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress:CAS@example.org");

        await browser.close();
    } catch (e) {
        failed = true;
        throw e;
    } finally {
        if (!failed) {
            await process.exit(0);
        }
    }
})();

