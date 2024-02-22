const puppeteer = require("puppeteer");
const assert = require("assert");
const cas = require("../../cas.js");

async function startAuthFlow(page, username) {
    await cas.log("Removing previous sessions and logging out");
    await cas.gotoLogout(page);
    
    await cas.log(`Starting authentication flow for ${username}`);
    await cas.gotoLogin(page);
    const pswd = await page.$("#password");
    assert(pswd === null);
    await cas.screenshot(page);
    await cas.type(page, "#username", username);
    await cas.pressEnter(page);
    await cas.waitForNavigation(page);
    await cas.logPage(page);
    await cas.screenshot(page);

    await cas.loginWith(page);
    await cas.logPage(page);
    await cas.screenshot(page);
    await cas.assertCookie(page);
    await cas.assertInnerTextStartsWith(page, "#content div p", "You, user3, have successfully logged in");

    await cas.click(page, "#auth-tab");
    await cas.waitForTimeout(page, 1000);
    await cas.screenshot(page);
    await cas.type(page, "#attribute-tab-1 input[type=search]", "surrogate");
    await cas.waitForTimeout(page, 1000);
    await cas.screenshot(page);
    await cas.waitForElement(page, "#surrogateUser td code kbd");
    await cas.assertInnerTextStartsWith(page, "#surrogateEnabled td code kbd", "[true]");
    await cas.assertInnerTextStartsWith(page, "#surrogatePrincipal td code kbd", "[casuser]");
    await cas.assertInnerTextStartsWith(page, "#surrogateUser td code kbd", "[user3]");
    await cas.waitForTimeout(page, 1000);
    await cas.screenshot(page);
}

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);

    await startAuthFlow(page, "user3+casuser-server");
    await startAuthFlow(page, "user3+casuser-none");
    await startAuthFlow(page, "user3+casuser-client");

    await browser.close();
})();
