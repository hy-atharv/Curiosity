mod services;
mod utils;
mod dto;
mod models;

use actix_web::{web, App, HttpResponse, HttpServer, Responder};
use reqwest::Client;

#[derive (Clone)]
struct AppState {
    web_client: Client
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {

    let http_client_build = Client::builder()
        .user_agent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/121.0.0.0")
        .connect_timeout(std::time::Duration::from_secs(2))
        .timeout(std::time::Duration::from_secs(5))
        .build();

    let http_client = http_client_build.unwrap_or_else(|e| {
        println!("{:?}", e.to_string());
        Client::new()
    });
    
    let state = AppState{
        web_client: http_client
    };

    HttpServer::new(move || {
        App::new()
            .app_data(web::Data::new(state.clone()))
            .route("/info", web::get().to(backend_info))
            .service(services::results::results)
    })
    .bind(("127.0.0.1", 3000))?
    .run()
    .await
}

async fn backend_info() -> impl Responder {
    HttpResponse::Ok().body("Curiosity Crawler is running...")
}